package fr.kazutoshi.locatedreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;
import fr.kazutoshi.locatedreminder.models.DatabaseHelper;
import fr.kazutoshi.locatedreminder.models.GlobalHelper;
import fr.kazutoshi.locatedreminder.models.SettingHelper;

/**
 * Created by Alex on 15/12/2015.
 */
public class LocatedReminderService extends Service {

  public static final String BROADCAST_ACTION = "ReminderLocation";
  private static final int TWO_MINUTES = 1000 * 60 * 2;
  public LocationManager locationManager;
  public LocationListener listener;
  public Location oldLocation;

  Intent intent;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("locatedreminder", "create service");
	  GlobalHelper.setDatabaseHelper(new DatabaseHelper(this));
	  AlarmHelper.loadAlarms();
    intent = new Intent(BROADCAST_ACTION);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("locatedreminder", "start service");
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    listener = new LocationListener();
	  String useNetwork = SettingHelper.getSettingValue("locationUpdateUseNetwork");
	  String useGPS = SettingHelper.getSettingValue("locationUpdateUseGPS");
	  String minTimeRefresh = SettingHelper.getSettingValue("locationUpdateMinTime");
	  String minDistanceRefresh = SettingHelper.getSettingValue("locationUpdateMinDistance");
	  if (useNetwork != null && useNetwork.equals("1")) {
		  locationManager.requestLocationUpdates(
						  LocationManager.NETWORK_PROVIDER,
						  minTimeRefresh == null ? 4000 : Integer.parseInt(minTimeRefresh),
						  minDistanceRefresh == null ? 0 : Integer.parseInt(minDistanceRefresh),
						  listener);
	  }
	  if (useGPS != null && useGPS.equals("1")) {
		  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						  minTimeRefresh == null ? 4000 : Integer.parseInt(minTimeRefresh),
						  minDistanceRefresh == null ? 0 : Integer.parseInt(minDistanceRefresh),
						  listener);
	  }
    return START_NOT_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  protected boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
      return true;
    }

    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    if (isSignificantlyNewer) {
      return true;
    } else if (isSignificantlyOlder) {
      return false;
    }

    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    boolean isFromSameProvider = isSameProvider(location.getProvider(),
            currentBestLocation.getProvider());

    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }

  public void onDestroy() {
    super.onDestroy();
    Log.d("locatedreminder", "stop service");
    locationManager.removeUpdates(listener);
  }



	private static double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515 * 1.609344 * 1000;

		return dist;
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}




  public class LocationListener implements android.location.LocationListener {

    @Override
    public synchronized void onLocationChanged(Location location) {
      /*intent.putExtra("Latitude", location.getLatitude());
      intent.putExtra("Longitude", location.getLongitude());
      intent.putExtra("Provider", location.getProvider());
      sendBroadcast(intent);*/
      for (AlarmHelper alarm : AlarmHelper.getAllAlarms()) {
	      if (alarm.isEnabled()
            && ((alarm.isIn() && distance(alarm.getLocationX(), alarm.getLocationY(),
            location.getLatitude(), location.getLongitude()) <= alarm.getRadius())
            || (!alarm.isIn() && distance(alarm.getLocationX(), alarm.getLocationY(),
            location.getLatitude(), location.getLongitude()) > alarm.getRadius()))) {
		      if (alarm.isNotification()) {
			      NotificationManager notificationManager =
							      (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			      Intent intent = new Intent(LocatedReminderService.this, HomeActivity.class);
			      PendingIntent pendingIntent = PendingIntent.getActivity(
							      LocatedReminderService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			      NotificationCompat.Builder builder =
							      new NotificationCompat.Builder(LocatedReminderService.this)
											      .setSmallIcon(R.drawable.notification_template_icon_bg)
											      .setContentTitle("Alarme")
											      .setContentText(alarm.getName())
											      .setContentIntent(pendingIntent);
			      notificationManager.notify(0, builder.build());
			      Vibrator v = (Vibrator) LocatedReminderService.this.getSystemService(VIBRATOR_SERVICE);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			      Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			      r.play();

			      for (int i = 0; i < alarm.getVibrationRepeatCount(); i++) {
				      v.vibrate(new long[]{
								      0,
								      alarm.getVibrationLength() * 100,
								      alarm.getVibrationLength() * 100
				      }, -1);
				      try {
					      synchronized (v) {
						      v.wait(alarm.getVibrationLength() * 100 * 2);
					      }
				      } catch (InterruptedException e) {
				      }
			      }
		      }

		      if (alarm.isSMS()) {
			      SmsManager smsManager = SmsManager.getDefault();
			      String contactsString = alarm.getSMSContacts();
			      if (!contactsString.isEmpty() && !alarm.getSMSContent().isEmpty()) {
				      String[] contacts = contactsString.split(";");
				      for (String contact : contacts)
					      smsManager.sendTextMessage(contact, null, alarm.getSMSContent(), null, null);
			      }
		      }

		      alarm.off().save();
	      }
      }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      //Log.d("locatedreminder", "GPS disabled");
      //Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
      //Log.d("locatedreminder", "GPS enabled");
      //Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
  }
}

