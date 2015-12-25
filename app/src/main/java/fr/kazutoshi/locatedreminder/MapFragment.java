package fr.kazutoshi.locatedreminder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;
import fr.kazutoshi.locatedreminder.models.SettingHelper;

/**
 * Created by Alex on 14/12/2015.
 */
public class MapFragment extends Fragment {
  MapView mapView;
  private AlarmHelper alarm;
  private GoogleMap googleMap;
  private Marker marker;
  private Circle radiusCircle;
  private boolean initialized = false;
  private double radius;

  private static class Popup {

    private final static String REMOVE_ITEM = "Supprimer";

    private static android.support.v7.app.AlertDialog.Builder dialog;

    public static void MarkerOptions(Context context, final Marker marker, final Circle circle) {
      if (dialog == null)
        dialog = new android.support.v7.app.AlertDialog.Builder(context);

      final CharSequence[] items = new CharSequence[] {
              REMOVE_ITEM
      };

      dialog.setTitle("Options").setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          if (items[which].equals(REMOVE_ITEM)) {
            marker.remove();
            circle.remove();
          }
        }
      });

    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.activity_map_fragment, container, false);
    mapView = (MapView) v.findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.onResume();

    marker = null;

    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }

    googleMap = mapView.getMap();
    googleMap.setMyLocationEnabled(true);
    LocationManager manager =
            (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    locate(location);

    radius = 20;
    googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
          setMarkerLocation(latLng);
        }
    });

    if (alarm != null) {
        radius = alarm.getRadius();
        setMarkerLocation(new LatLng(alarm.getLocationX(), alarm.getLocationY()), 17.0f);
    }

    return v;
  }

  public void setMarkerLocation(LatLng latLng) {
    setMarkerLocation(latLng, null);
  }

  public void setMarkerLocation(LatLng latLng, Float zoom) {
    if (marker != null) {
      marker.remove();
      radiusCircle.remove();
    }
    marker = googleMap.addMarker(
        new MarkerOptions().position(latLng));
    centerOnLatLng(latLng, zoom);

	  String strokeColor = SettingHelper.getSettingValue("mapCircleStrokeColor");
	  if (strokeColor == null)
		  strokeColor = "FF425C97";
	  String fillColor = SettingHelper.getSettingValue("mapCircleFillColor");
	  if (fillColor == null)
		  fillColor = "1E425C97";

    radiusCircle = googleMap.addCircle(
          new CircleOptions().center(latLng).radius(radius)
                .strokeColor(Color.parseColor("#" + strokeColor))
                .fillColor(Color.parseColor("#" + fillColor))
                .strokeWidth(2));
  }

  private void locate(Location location) {
    if (location != null)
      if (!initialized) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
            new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
        initialized = true;
      }
  }

  public MapFragment centerOnLatLng(LatLng latLng, Float zoom) {
    if (latLng != null)
      if (zoom == null) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
      } else {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
      }
    return this;
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }



  public Marker getMarker() {
      return marker;
  }

  public double getRadius() {
    if (radiusCircle != null)
      return radiusCircle.getRadius();
    return 0;
  }

  public MapFragment increaseRadius(double plusplus) {
      return changeRadius(plusplus);
  }

  public MapFragment decreaseRadius(double moinsmoins) {
    return changeRadius(-moinsmoins);
  }

  public MapFragment changeRadius(double changechange) {
    if (radiusCircle != null) {
      if (radius + changechange < 0)
        radius = 0;
      else
        radius += changechange;
      radiusCircle.setRadius(radius);
    }
    return this;
  }

  public MapFragment setAlarm(AlarmHelper alarm) {
    if (alarm != null) {
      this.alarm = alarm;
      /*radius = alarm.getRadius();
      setMarkerLocation(new LatLng(alarm.getLocationX(), alarm.getLocationY()));*/
    }

    return this;
  }
}
