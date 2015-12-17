package fr.kazutoshi.locatedreminder;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;

public class CreateReminderActivity extends AppCompatActivity {

    private MapFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);
        addMapFragment();
        findViewById(R.id.addReminderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker marker = fragment.getMarker();
                if (marker != null) {
                    LatLng latLng = marker.getPosition();
                    new AlarmHelper(-1, latLng.latitude, latLng.longitude,
                            fragment.getRadius(), true).save();
                }
                finish();
            }
        });

        findViewById(R.id.decreaseRadius).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.decreaseRadius(1);
            }
        });

        findViewById(R.id.increaseRadius).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.increaseRadius(1);
            }
        });
    }

    private void addMapFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new MapFragment();
        transaction.add(R.id.mapView, fragment);
        transaction.commit();
    }
}
