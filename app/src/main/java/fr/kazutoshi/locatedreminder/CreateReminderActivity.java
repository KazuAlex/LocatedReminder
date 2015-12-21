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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

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


	  if (getIntent().hasExtra("ALARM_ID"))
		  alarm = AlarmHelper.getFromId(getIntent().getLongExtra("ALARM_ID", 0));

    addMapFragment(alarm);

		optionsIsShowing = false;

    findViewById(R.id.addReminderButton).setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
	    Marker marker = fragment.getMarker();
	    if (marker != null) {
		    LatLng latLng = marker.getPosition();
		    EditText editTextName = (EditText) findViewById(R.id.alarmName);
		    EditText editTextVibrationLength =
						    (EditText) findViewById(R.id.alarmVibrationLength);
		    EditText editTextVibrationRepeatCount =
						    (EditText) findViewById(R.id.alarmVibrationRepeatCount);
				if (alarm == null) {
					alarm = new AlarmHelper(-1, editTextName.getText().toString(),
									latLng.latitude, latLng.longitude,
									fragment.getRadius(), true)
									.setVibrationLength(Integer.valueOf(editTextVibrationLength.getText().toString()))
									.setVibrationRepeatCount(
													Integer.valueOf(editTextVibrationRepeatCount.getText().toString()))
									.save();
				} else {
					alarm.setName(editTextName.getText().toString())
									.setLocationX(latLng.latitude)
									.setLocationY(latLng.longitude)
									.setRadius(fragment.getRadius())
									.setVibrationLength(Integer.valueOf(editTextVibrationLength.getText().toString()))
									.setVibrationRepeatCount(
													Integer.valueOf(editTextVibrationRepeatCount.getText().toString()))
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

	@Override
	protected void onStart() {
		super.onStart();
		if (alarm != null) {
			EditText editTextName = (EditText) findViewById(R.id.alarmName);
			editTextName.setText(alarm.getRawName(), EditText.BufferType.EDITABLE);

			EditText editTextVibrationLength = (EditText) findViewById(R.id.alarmVibrationLength);
			editTextVibrationLength.setText(
							String.valueOf(alarm.getVibrationLength()), EditText.BufferType.EDITABLE);

			EditText editTextVibrationRepeatCount =
							(EditText) findViewById(R.id.alarmVibrationRepeatCount);
			editTextVibrationRepeatCount.setText(
							String.valueOf(alarm.getVibrationRepeatCount()), EditText.BufferType.EDITABLE);
		}
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

	private void showReminderOptions() {
		final ScrollView reminderOptions = (ScrollView) findViewById(R.id.reminderOptions);
		if (optionsIsShowing) {
			Log.d("locatedreminder", "optionsIsShowing");
			optionsIsShowing = false;
			View view = this.getCurrentFocus();
			if (view != null) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}

			reminderOptions.animate().setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					reminderOptions.setVisibility(View.GONE);
				}
			}).translationY(-reminderOptions.getHeight());

		} else {
			Log.d("locatedreminder", "optionsIsHidden");
			optionsIsShowing = true;

			reminderOptions.setY(-reminderOptions.getHeight());
			reminderOptions.setVisibility(View.VISIBLE);

			reminderOptions.animate().setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
				}
			}).translationY(0);
		}
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
			showReminderOptions();
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
