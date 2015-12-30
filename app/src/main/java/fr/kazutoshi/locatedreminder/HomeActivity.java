package fr.kazutoshi.locatedreminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;
import fr.kazutoshi.locatedreminder.models.DatabaseHelper;
import fr.kazutoshi.locatedreminder.models.GlobalHelper;
import fr.kazutoshi.locatedreminder.models.SettingHelper;
import fr.kazutoshi.locatedreminder.view.AlarmView;

public class HomeActivity extends AppCompatActivity {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    startService(new Intent(this, LocatedReminderService.class));

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    GlobalHelper.setDatabaseHelper(new DatabaseHelper(this));


    /* INIT DEFAULTS SETTINGS VALUES IF NOT EXISTS */
	  if (SettingHelper.getSetting("mapCircleStrokeColor") == null)
		  new SettingHelper(-1, "mapCircleStrokeColor", "FF425C97").save();
	  if (SettingHelper.getSetting("mapCircleFillColor") == null)
		  new SettingHelper(-1, "mapCircleFillColor", "1E425C97").save();
	  if (SettingHelper.getSetting("defaultAlarmVibrationLength") == null)
		  new SettingHelper(-1, "defaultAlarmVibrationLength", "5").save();
    if (SettingHelper.getSetting("defaultAlarmVibrationRepeatCount") == null)
      new SettingHelper(-1, "defaultAlarmVibrationRepeatCount", "1").save();
    if (SettingHelper.getSetting("locationUpdateMinTime") == null)
      new SettingHelper(-1, "locationUpdateMinTime", "4000").save();
    if (SettingHelper.getSetting("locationUpdateMinDistance") == null)
      new SettingHelper(-1, "locationUpdateMinDistance", "0").save();
    if (SettingHelper.getSetting("locationUpdateUseNetwork") == null)
      new SettingHelper(-1, "locationUpdateUseNetwork", "0").save();
    if (SettingHelper.getSetting("locationUpdateUseGPS") == null)
      new SettingHelper(-1, "locationUpdateUseGPS", "1").save();


	  if (SettingHelper.getSetting("doNotShowAgainStartingPopup") == null) {
		  AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		  LinearLayout layout = new LinearLayout(this);
		  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						  ViewGroup.LayoutParams.MATCH_PARENT,
						  ViewGroup.LayoutParams.WRAP_CONTENT);
		  layout.setLayoutParams(params);
		  layout.setOrientation(LinearLayout.VERTICAL);
		  layout.setPadding(30, 30, 30, 30);


		  LinearLayout layoutSettings = new LinearLayout(this);
		  params = new LinearLayout.LayoutParams(
						  ViewGroup.LayoutParams.MATCH_PARENT,
						  ViewGroup.LayoutParams.WRAP_CONTENT);
		  layoutSettings.setLayoutParams(params);
		  layoutSettings.setOrientation(LinearLayout.HORIZONTAL);
		  layoutSettings.setPadding(0, 20, 0, 20);
		  layoutSettings.setGravity(Gravity.CENTER_VERTICAL);

		  ImageView imageView = new ImageView(this);
		  params = new LinearLayout.LayoutParams(100, 100);
		  imageView.setLayoutParams(params);
		  imageView.setImageResource(R.drawable.ic_settings_white_36dp);
		  imageView.setBackgroundColor(Color.BLUE);

		  TextView textView = new TextView(this);
		  textView.setText("Vous permet de régler les paramètres par défaut des notifications " +
						  "et les paramètres globaux de l'application");
		  textView.setPadding(20, 0, 0, 0);

		  layoutSettings.addView(imageView);
		  layoutSettings.addView(textView);

		  layout.addView(layoutSettings);


		  LinearLayout layoutAlarm = new LinearLayout(this);
		  params = new LinearLayout.LayoutParams(
						  ViewGroup.LayoutParams.MATCH_PARENT,
						  ViewGroup.LayoutParams.WRAP_CONTENT);
		  layoutAlarm.setLayoutParams(params);
		  layoutAlarm.setOrientation(LinearLayout.HORIZONTAL);
		  layoutAlarm.setPadding(0, 20, 0, 20);
		  layoutAlarm.setGravity(Gravity.CENTER_VERTICAL);

		  textView = new TextView(this);
		  textView.setText("Le bouton \"Ajouter un alarme\" vous permet de créer une alarme");

		  layoutAlarm.addView(textView);

		  layout.addView(layoutAlarm);


		  layoutAlarm = new LinearLayout(this);
		  params = new LinearLayout.LayoutParams(
						  ViewGroup.LayoutParams.MATCH_PARENT,
						  ViewGroup.LayoutParams.WRAP_CONTENT);
		  layoutAlarm.setLayoutParams(params);
		  layoutAlarm.setOrientation(LinearLayout.HORIZONTAL);
		  layoutAlarm.setPadding(0, 20, 0, 20);

		  textView = new TextView(this);
		  textView.setText("Pour éditer une alarme, il suffit de cliquer dessus");

		  layoutAlarm.addView(textView);

		  layout.addView(layoutAlarm);


		  layoutAlarm = new LinearLayout(this);
		  params = new LinearLayout.LayoutParams(
						  ViewGroup.LayoutParams.MATCH_PARENT,
						  ViewGroup.LayoutParams.WRAP_CONTENT);
		  layoutAlarm.setLayoutParams(params);
		  layoutAlarm.setOrientation(LinearLayout.HORIZONTAL);
		  layoutAlarm.setPadding(0, 20, 0, 20);
		  layoutAlarm.setGravity(Gravity.CENTER_VERTICAL);

		  imageView = new ImageView(this);
		  params = new LinearLayout.LayoutParams(100, 100);
		  imageView.setLayoutParams(params);
		  imageView.setImageResource(R.drawable.ic_info_outline_white_36dp);
		  imageView.setBackgroundColor(Color.BLUE);

		  textView = new TextView(this);
		  textView.setText("Vous permet de régler les paramètres spécifiques à l'alarme.\n" +
						  "Vous pouvez aussi choisir (et paramétrer) si l'alarme doit vous envoyer " +
						  "une notification ou envoyer un sms à des personnes en particulier.");
		  textView.setPadding(20, 0, 0, 0);

		  layoutAlarm.addView(imageView);
		  layoutAlarm.addView(textView);

		  layout.addView(layoutAlarm);




		  dialog.setTitle("Information")
						  .setView(layout)
						  .setPositiveButton("Ne plus afficher", new DialogInterface.OnClickListener() {
							  @Override
							  public void onClick(DialogInterface dialog, int which) {
								  new SettingHelper(-1, "doNotShowAgainStartingPopup", "").save();
								  dialog.dismiss();
							  }
						  }).setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  dialog.cancel();
			  }
		  }).show();
	  }
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopService(new Intent(this, LocatedReminderService.class));
  }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class AlarmsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final int CREATE_REMINDER = 0;

    private boolean updateAlarmsViews = false;

    public void invalidateViews() {
	    updateAlarmsViews = true;
    }

    public AlarmsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AlarmsFragment newInstance() {
        AlarmsFragment fragment = new AlarmsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_home, container, false);
      rootView.findViewById(R.id.newAlarmButton).setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent = new Intent(getActivity(), CreateReminderActivity.class);
                      startActivityForResult(intent, CREATE_REMINDER);
                  }
              });
      loadAlarms(rootView);
      return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CREATE_REMINDER) {
		    if (resultCode == RESULT_OK) {
			    if (data.getBooleanExtra("UPDATE_VIEWS", false)) {
				    invalidateViews();
			    }
		    }
	    }
    }

    @Override
    public void onResume() {
	    super.onResume();
	    if (updateAlarmsViews) {
		    updateAlarmsViews = false;
		    loadAlarms(getView());
	    }
    }

    private void loadAlarms(View rootView) {
	    final LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.listAlarms);
	    runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
			    layout.removeAllViews();

			    HashSet<AlarmHelper> alarms = AlarmHelper.getAllAlarms();

			    for (final AlarmHelper alarm : alarms) {
				    AlarmView alarmView = new AlarmView(getActivity(), alarm);
				    alarmView.setLayoutParams(new LinearLayout.LayoutParams(
								    LinearLayout.LayoutParams.MATCH_PARENT,
								    LinearLayout.LayoutParams.WRAP_CONTENT));
            alarmView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent intent = new Intent(
                    getActivity(), CreateReminderActivity.class);
                intent.putExtra("ALARM_ID", alarm.getId());
                startActivityForResult(intent, CREATE_REMINDER);
              }
            });
            layout.addView(alarmView);
			    }
		    }
	    });
    }

    private void runOnUiThread(Runnable runnable) {
          getActivity().runOnUiThread(runnable);
      }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

      public SectionsPagerAdapter(FragmentManager fm) {
          super(fm);
      }

      @Override
      public Fragment getItem(int position) {
          // getItem is called to instantiate the fragment for the given page.
          // Return a AlarmsFragment (defined as a static inner class below).
          return AlarmsFragment.newInstance();
      }

      @Override
      public int getCount() {
          // Show 1 total pages.
          return 1;
      }

      @Override
      public CharSequence getPageTitle(int position) {
          switch (position) {
              case 0:
                  return "SECTION 1";
          }
          return null;
      }
  }
}
