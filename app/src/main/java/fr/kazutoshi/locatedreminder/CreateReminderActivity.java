package fr.kazutoshi.locatedreminder;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;

public class CreateReminderActivity extends AppCompatActivity {

  private MapFragment fragment;
	private boolean optionsIsVisible;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_fragment);
    addMapFragment();

	  final EditText editTextName = (EditText) findViewById(R.id.alarmName);

    findViewById(R.id.addReminderButton).setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		    Marker marker = fragment.getMarker();
		    if (marker != null) {
			    LatLng latLng = marker.getPosition();
			    new AlarmHelper(-1, editTextName.getText().toString(), latLng.latitude, latLng.longitude,
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

	  optionsIsVisible = false;
	  final LinearLayout dropdownReminderOptions =
					  (LinearLayout) findViewById(R.id.dropdownReminderOptions);
	  final RelativeLayout reminderOptions = (RelativeLayout) findViewById(R.id.reminderOptions);
	  final ScrollView reminderOptionsScrollView =
					  (ScrollView) findViewById(R.id.reminderOptionsScrollView);

    dropdownReminderOptions.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		    RelativeLayout.LayoutParams reminderOptionsParams =
						    (RelativeLayout.LayoutParams) reminderOptions.getLayoutParams();
		    RelativeLayout.LayoutParams dropdownParams =
						    (RelativeLayout.LayoutParams) dropdownReminderOptions.getLayoutParams();
		    if (reminderOptionsParams == null) {
			    reminderOptionsParams = new RelativeLayout.LayoutParams(
							    ViewGroup.LayoutParams.MATCH_PARENT,
							    ViewGroup.LayoutParams.WRAP_CONTENT);
		    }
		    if (dropdownParams == null)
			    dropdownParams = new RelativeLayout.LayoutParams(
							    ViewGroup.LayoutParams.MATCH_PARENT,
							    ViewGroup.LayoutParams.WRAP_CONTENT);
		    if (optionsIsVisible) {
			    dropdownParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			    reminderOptionsParams.addRule(RelativeLayout.ABOVE, R.id.buttons);
			    reminderOptionsScrollView.setVisibility(View.VISIBLE);
		    } else {
			    dropdownParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			    reminderOptionsParams.removeRule(RelativeLayout.ABOVE);
			    reminderOptionsScrollView.setVisibility(View.GONE);
		    }
		    optionsIsVisible = !optionsIsVisible;
		    dropdownReminderOptions.setLayoutParams(dropdownParams);
		    reminderOptions.setLayoutParams(reminderOptionsParams);
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

	public void finish() {
		Intent data = new Intent();
		data.putExtra("UPDATE_VIEWS", true);
		if (getParent() != null) {
			getParent().setResult(RESULT_OK, data);
		} else {
			setResult(RESULT_OK, data);
		}
		super.finish();
	}
}
