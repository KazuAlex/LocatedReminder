package fr.kazutoshi.locatedreminder;

import android.content.Context;
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

/**
 * Created by Alex on 14/12/2015.
 */
public class MapFragment extends Fragment {
    MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private Circle radiusCircle;
    private boolean initialized = false;
    private double radius;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savecInstanceState) {
        View v = inflater.inflate(R.layout.activity_map_fragment, container, false);
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savecInstanceState);
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
        /*googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                locate(location);
            }
        });*/
        radius = 20;
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                    radiusCircle.remove();
                }
                marker = googleMap.addMarker(
                        new MarkerOptions().position(latLng).title("Test marker"));
                radiusCircle = googleMap.addCircle(
                        new CircleOptions().center(latLng).radius(radius)
                                .strokeColor(Color.argb(255, 66, 92, 151))
                                .fillColor(Color.argb(30, 66, 92, 151))
                                .strokeWidth(2));
            }
        });

        return v;
    }

    private void locate(Location location) {
        if (location != null)
            if (!initialized) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
                initialized = true;
            }
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
}
