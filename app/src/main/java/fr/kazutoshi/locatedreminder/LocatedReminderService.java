package fr.kazutoshi.locatedreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;
import fr.kazutoshi.locatedreminder.models.DatabaseHelper;
import fr.kazutoshi.locatedreminder.models.GlobalHelper;

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
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
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
	      if (alarm.isEnabled() && distance(alarm.getLocationX(), alarm.getLocationY(),
					      location.getLatitude(), location.getLongitude()) <= alarm.getRadius()) {
		      NotificationManager notificationManager =
						      (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		      Intent intent = new Intent(LocatedReminderService.this, HomeActivity.class);
		      PendingIntent pendingIntent = PendingIntent.getActivity(
						      LocatedReminderService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		      NotificationCompat.Builder builder =
						      new NotificationCompat.Builder(LocatedReminderService.this)
										      .setSmallIcon(R.drawable.notification_template_icon_bg)
										      .setContentTitle("New Reminder")
										      .setContentText(alarm.getName())
										      .setContentIntent(pendingIntent);
		      notificationManager.notify(0, builder.build());
		      Vibrator v = (Vibrator) LocatedReminderService.this.getSystemService(VIBRATOR_SERVICE);
		      v.vibrate(500);
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

