package fr.kazutoshi.locatedreminder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;
import fr.kazutoshi.locatedreminder.models.DatabaseHelper;
import fr.kazutoshi.locatedreminder.models.GlobalHelper;

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
					    final LinearLayout alarmLayout = new LinearLayout(getActivity());
					    alarmLayout.setLayoutParams(new LinearLayout.LayoutParams(
									    ViewGroup.LayoutParams.MATCH_PARENT,
									    ViewGroup.LayoutParams.WRAP_CONTENT));
					    alarmLayout.setOrientation(LinearLayout.VERTICAL);
					    TextView alarmView = new TextView(getActivity());
					    alarmView.setText(alarm.getName());
					    LinearLayout buttonsLayout = new LinearLayout(getActivity());
					    buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
									    ViewGroup.LayoutParams.MATCH_PARENT,
									    ViewGroup.LayoutParams.WRAP_CONTENT
					    ));
					    buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
					    Button enabledButton = new Button(getActivity());
					    enabledButton.setText(alarm.isEnabled() ? "ON" : "OFF");
					    enabledButton.setOnClickListener(new View.OnClickListener() {
						    @Override
						    public void onClick(View v) {
							    Button button = (Button) v;
							    if (alarm.isEnabled())
								    button.setText("OFF");
							    else
								    button.setText("ON");
							    alarm.toggle();
						    }
					    });
					    Button removeButton = new Button(getActivity());
					    removeButton.setText("X");
					    removeButton.setOnClickListener(new View.OnClickListener() {
						    @Override
						    public void onClick(View v) {
							    alarm.delete();
							    layout.removeView(alarmLayout);
						    }
					    });
					    buttonsLayout.addView(enabledButton);
					    buttonsLayout.addView(removeButton);
					    alarmLayout.addView(alarmView);
					    alarmLayout.addView(buttonsLayout);
					    layout.addView(alarmLayout);
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
