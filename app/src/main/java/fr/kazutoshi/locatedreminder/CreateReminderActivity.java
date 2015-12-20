package fr.kazutoshi.locatedreminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;

public class CreateReminderActivity extends AppCompatActivity {

  private MapFragment fragment;
  private AlarmHelper alarm;
	private boolean optionsIsShowing;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_fragment);
		if (getIntent().hasExtra("ALARM_ID")) {
			RelativeLayout reminderOptions = (RelativeLayout) findViewById(R.id.reminderOptions);
			alarm = AlarmHelper.getFromId(getIntent().getLongExtra("ALARM_ID", 0));
			Log.d("locatedreminder", "alarm_id : " + alarm.getId());
			if (alarm != null) {
				Log.d("locatedreminder", "alarm != null on create");
				reminderOptions.setVisibility(View.VISIBLE);
				EditText editTextName = (EditText) findViewById(R.id.alarmName);
				editTextName.setText(alarm.getRawName(), EditText.BufferType.EDITABLE);
				reminderOptions.setVisibility(View.GONE);
			}
		}
    addMapFragment(alarm);

		optionsIsShowing = true;

		if (alarm != null) {
		}

    findViewById(R.id.addReminderButton).setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		    Marker marker = fragment.getMarker();
		    if (marker != null) {
			    LatLng latLng = marker.getPosition();
			    EditText editTextName = (EditText) findViewById(R.id.alarmName);
					if (alarm == null) {
						alarm = new AlarmHelper(-1, editTextName.getText().toString(), latLng.latitude, latLng.longitude,
								fragment.getRadius(), true).save();
					} else {
						alarm.setName(editTextName.getText().toString())
								.setLocationX(latLng.latitude)
								.setLocationY(latLng.longitude)
								.setRadius(fragment.getRadius())
								.save();
					}
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

	private void addMapFragment(AlarmHelper alarm) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		fragment = new MapFragment();
		transaction.add(R.id.mapView, fragment);
		transaction.commit();
		if (alarm != null)
			fragment.setAlarm(alarm);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_create_reminder, menu);
		return true;
	}

	private void showSearchInput() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Search");

		final EditText searchInput = new EditText(this);
		searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(searchInput);

		builder.setPositiveButton("Rechercher", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				search(searchInput.getText().toString());
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	private void search(String address) {
		Geocoder geocoder = new Geocoder(this);
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocationName(address, 1);
			if (addresses.size() > 0)
				fragment.setMarkerLocation(
								new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
		} catch (IOException e) {
			Log.e("locatedreminder", e.toString());
			for (StackTraceElement ste : e.getStackTrace())
				Log.e("locatedreminder", ste.toString());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_search) {
			showSearchInput();
			return true;
		} else if (id == R.id.action_extend_infos) {
			final RelativeLayout reminderOptions = (RelativeLayout) findViewById(R.id.reminderOptions);
			if (optionsIsShowing) {
				//reminderOptions.setVisibility(View.GONE);
				Log.d("locationreminder", "optionsIsShowing");
				optionsIsShowing = !optionsIsShowing;
				View view = this.getCurrentFocus();
				if (view != null) {
					InputMethodManager imm =
							(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				reminderOptions.animate().setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						Log.d("locatedreminder", "onanimationend");
						reminderOptions.setVisibility(View.GONE);
					}
				}).translationY(-reminderOptions.getHeight()).start();
			} else {
				//reminderOptions.setVisibility(View.VISIBLE);
				reminderOptions.setY(-reminderOptions.getHeight());
				reminderOptions.animate()
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								super.onAnimationEnd(animation);
								Log.d("locatedreminder", "onanimationend");
								optionsIsShowing = !optionsIsShowing;
							}

							@Override
							public void onAnimationStart(Animator animation) {
								super.onAnimationStart(animation);
								reminderOptions.setVisibility(View.VISIBLE);
							}
						}).translationY(0).start();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
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
