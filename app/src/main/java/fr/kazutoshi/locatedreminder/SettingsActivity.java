package fr.kazutoshi.locatedreminder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import fr.kazutoshi.locatedreminder.models.SettingHelper;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsActivity extends AppCompatActivity {

	private static class Popup {

		private static final String COLOR_PICKER = "Color picker";
		private static final String HEXA = "Hexadecimal";
		private static final String RGB = "RGB";
		private static final String CLOSE = "Fermer";

		public static void showMapCircleColorOptions(final Context context) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);

			final CharSequence[] items = new CharSequence[] {
							COLOR_PICKER,
							HEXA,
							RGB,
							CLOSE
			};

			dialog.setTitle("Editer la couleur").setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String strColor = SettingHelper.getSettingValue("mapCircleStrokeColor");
					int color;
					if (strColor == null)
						color = Color.parseColor("#425C97");
					else
						color = Color.parseColor("#" + strColor.substring(2));
					if (items[which].equals(COLOR_PICKER)) {
						new AmbilWarnaDialog(context, color, new AmbilWarnaDialog.OnAmbilWarnaListener() {

							@Override
							public void onOk(AmbilWarnaDialog dialog, int color) {
								int r = Color.red(color);
								int g = Color.green(color);
								int b = Color.blue(color);

								SettingHelper circleStrokeColor = SettingHelper.getSetting("mapCircleStrokeColor");
								SettingHelper circleFillColor = SettingHelper.getSetting("mapCircleFillColor");

								if (circleStrokeColor == null)
									new SettingHelper(-1, "mapCircleStrokeColor",
													String.format("FF%02x%02x%02x", r, g, b)).save();
								else
									circleStrokeColor.setValue(String.format("FF%02x%02x%02x", r, g, b)).save();

								if (circleFillColor == null)
									new SettingHelper(-1, "mapCircleFillColor",
													String.format("FF%02x%02x%02x", r, g, b)).save();
								else
									circleFillColor.setValue(String.format("1E%02x%02x%02x", r, g, b)).save();

							}

							@Override
							public void onCancel(AmbilWarnaDialog dialog) {
							}
						}).show();
					} else if (items[which].equals(HEXA)) {
						AlertDialog.Builder dialogHexa = new AlertDialog.Builder(context);
						final EditText editTextHexa = new EditText(context);
						editTextHexa.setLayoutParams(new LinearLayout.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
						if (strColor == null)
							editTextHexa.setText("425C97");
						else
							editTextHexa.setText(strColor.substring(2));

						InputFilter inputFilter = new InputFilter() {
							@Override
							public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
								if (source.length() != 6 && editTextHexa.length() >= 6)
									return "";

								String allowedCharacters = "0123456789ABCDEF";

								for (int i = start; i < end; i++)
									if (!allowedCharacters.contains(("" + source.charAt(i)).toUpperCase()))
										return "";

								return null;
							}
						};
						editTextHexa.setFilters(new InputFilter[]{inputFilter});
						editTextHexa.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

						dialogHexa.setTitle("Valeur hexadécimale")
										.setView(editTextHexa)
										.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												if (editTextHexa.getText().length() != 6) {
													editTextHexa.setBackgroundColor(Color.parseColor("#1EFF0000"));
													return;
												}

												SettingHelper circleStrokeColor = SettingHelper.getSetting("mapCircleStrokeColor");
												SettingHelper circleFillColor = SettingHelper.getSetting("mapCircleFillColor");

												if (circleStrokeColor == null)
													new SettingHelper(-1, "mapCircleStrokeColor",
																	editTextHexa.getText().toString()).save();
												else
													circleStrokeColor.setValue(editTextHexa.getText().toString()).save();

												if (circleFillColor == null)
													new SettingHelper(-1, "mapCircleFillColor",
																	editTextHexa.getText().toString()).save();
												else
													circleFillColor.setValue(editTextHexa.getText().toString()).save();

												dialog.dismiss();
											}
										}).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();
											}
										}).show();
					} else if (items[which].equals(RGB)) {
						AlertDialog.Builder dialogHexa = new AlertDialog.Builder(context);
						final LinearLayout layout = new LinearLayout(context);
						final EditText editTextRed = new EditText(context);
						final EditText editTextGreen = new EditText(context);
						final EditText editTextBlue = new EditText(context);

						layout.setLayoutParams(new LinearLayout.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3));

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params.weight = 1;
						editTextRed.setLayoutParams(params);

						params = new LinearLayout.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params.weight = 1;
						editTextGreen.setLayoutParams(params);

						params = new LinearLayout.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params.weight = 1;
						editTextBlue.setLayoutParams(params);

						InputFilter inputFilter = new InputFilter() {

							private int min = 0, max = 255;

							@Override
							public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
								try {
									int input = Integer.parseInt(dest.toString() + source.toString());
									if (isInRange(min, max, input))
										return null;
								} catch (NumberFormatException nfe) { }
								return "";
							}

							private boolean isInRange(int a, int b, int c) {
								return b > a ? c >= a && c <= b : c >= b && c <= a;
							}
						};
						editTextRed.setFilters(
										new InputFilter[]{ inputFilter });
						editTextRed.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
						editTextGreen.setFilters(
										new InputFilter[]{inputFilter });
						editTextGreen.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
						editTextBlue.setFilters(
										new InputFilter[]{inputFilter });
						editTextBlue.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

						layout.addView(editTextRed);
						layout.addView(editTextGreen);
						layout.addView(editTextBlue);

						dialogHexa.setTitle("Valeur hexadécimale")
										.setView(layout)
										.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												SettingHelper circleStrokeColor =
																SettingHelper.getSetting("mapCircleStrokeColor");
												SettingHelper circleFillColor =
																SettingHelper.getSetting("mapCircleFillColor");

												int r = Integer.parseInt(editTextRed.getText().toString());
												int g = Integer.parseInt(editTextGreen.getText().toString());
												int b = Integer.parseInt(editTextBlue.getText().toString());

												String color = String.format("%02x%02x%02x", r, g, b);

												if (circleStrokeColor == null)
													new SettingHelper(-1, "mapCircleStrokeColor", "FF" + color).save();
												else
													circleStrokeColor.setValue("FF" + color).save();

												if (circleFillColor == null)
													new SettingHelper(-1, "mapCircleFillColor", "1E" + color).save();
												else
													circleFillColor.setValue("1E" + color).save();

												dialog.dismiss();
											}
										}).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}).show();
					}
					dialog.dismiss();
				}
			}).show();

		}

		public static void showEditSettingValue(final Context context, final SettingHelper setting) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			final EditText editTextNewValue = new EditText(context);
			editTextNewValue.setText(setting.getValue());
			dialog.setTitle("Modification des paramères")
						.setView(editTextNewValue)
						.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setting.setValue(editTextNewValue.getText().toString()).save();
							}
						}).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

		final TextView mapCircleColor = (TextView) findViewById(R.id.mapCircleColor);
		SettingHelper mapCircleStrokeColor = SettingHelper.getSetting("mapCircleStrokeColor");
		String mapCircleStrokeColorString = "";
		if (mapCircleStrokeColor == null)
			mapCircleStrokeColor = new SettingHelper(-1, "mapCircleStrokeColor", "FF425C97").save();

		mapCircleStrokeColor.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				String mapCircleStrokeColorString = "#" + value.substring(2);
				mapCircleColor.setText(mapCircleStrokeColorString);
			}
		});
		mapCircleStrokeColorString = "#" + mapCircleStrokeColor.getValue().substring(2);
		mapCircleColor.setText(mapCircleStrokeColorString);
		mapCircleColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popup.showMapCircleColorOptions(SettingsActivity.this);
			}
		});

		SettingHelper defaultAlarmVibrationLength =
						SettingHelper.getSetting("defaultAlarmVibrationLength");
		final TextView editTextDefaultAlarmVibrationLength =
						(TextView) findViewById(R.id.defaultAlarmVibrationLength);
		if (defaultAlarmVibrationLength == null)
			defaultAlarmVibrationLength =
							new SettingHelper(-1, "defaultAlarmVibrationLength", "5").save();
		final SettingHelper finalDefaultAlarmVibrationLength = defaultAlarmVibrationLength;
		defaultAlarmVibrationLength.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				editTextDefaultAlarmVibrationLength.setText(value);
			}
		});
		editTextDefaultAlarmVibrationLength.setText(defaultAlarmVibrationLength.getValue());
		editTextDefaultAlarmVibrationLength.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popup.showEditSettingValue(SettingsActivity.this, finalDefaultAlarmVibrationLength);
			}
		});

		SettingHelper defaultAlarmVibrationRepeatCount =
						SettingHelper.getSetting("defaultAlarmVibrationRepeatCount");
		final TextView editTextDefaultAlarmVibrationRepeatCount =
						(TextView) findViewById(R.id.defaultAlarmVibrationRepeatCount);
		if (defaultAlarmVibrationRepeatCount == null)
			defaultAlarmVibrationRepeatCount =
							new SettingHelper(-1, "defaultAlarmVibrationRepeatCount", "1");
		final SettingHelper finalDefaultAlarmVibrationRepeatCount = defaultAlarmVibrationRepeatCount;
		defaultAlarmVibrationRepeatCount.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				editTextDefaultAlarmVibrationRepeatCount.setText(value);
			}
		});
		editTextDefaultAlarmVibrationRepeatCount.setText(defaultAlarmVibrationRepeatCount.getValue());
		editTextDefaultAlarmVibrationRepeatCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popup.showEditSettingValue(SettingsActivity.this, finalDefaultAlarmVibrationRepeatCount);
			}
		});

		SettingHelper useGPS = SettingHelper.getSetting("locationUpdateUseGPS");
		final Switch switchUseGPS = (Switch) findViewById(R.id.switchUseGPS);
		if (useGPS == null)
			useGPS = new SettingHelper(-1, "locationUpdateUseGPS", "1");
		useGPS.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				switchUseGPS.setChecked(value.equals("1"));
				stopService(new Intent(SettingsActivity.this, LocatedReminderService.class));
				startService(new Intent(SettingsActivity.this, LocatedReminderService.class));
			}
		});
		final SettingHelper finalUseGPS = useGPS;
		switchUseGPS.setChecked(useGPS.getValue().equals("1"));
		switchUseGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				finalUseGPS.setValue(isChecked ? "1" : "0");
				stopService(new Intent(SettingsActivity.this, LocatedReminderService.class));
				startService(new Intent(SettingsActivity.this, LocatedReminderService.class));
			}
		});

		SettingHelper useNetwork = SettingHelper.getSetting("locationUpdateUseNetwork");
		final Switch switchUseNetwork = (Switch) findViewById(R.id.switchUseNetwork);
		if (useNetwork == null)
			useNetwork = new SettingHelper(-1, "locationUpdateUseNetwork", "1");
		useNetwork.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				switchUseNetwork.setChecked(value.equals("1"));
				stopService(new Intent(SettingsActivity.this, LocatedReminderService.class));
				startService(new Intent(SettingsActivity.this, LocatedReminderService.class));
			}
		});
		final SettingHelper finalUseNetwork = useNetwork;
		switchUseNetwork.setChecked(useNetwork.getValue().equals("1"));
		switchUseNetwork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				finalUseNetwork.setValue(isChecked ? "1" : "0");
				stopService(new Intent(SettingsActivity.this, LocatedReminderService.class));
				startService(new Intent(SettingsActivity.this, LocatedReminderService.class));
			}
		});

		SettingHelper minTimeRefresh = SettingHelper.getSetting("locationUpdateMinTime");
		final TextView editTextMinTimeRefresh = (TextView) findViewById(R.id.textViewMinTimeRefresh);
		final LinearLayout layoutMinTimeRefresh =
						(LinearLayout) findViewById(R.id.layoutMinTimeRefresh);
		if (minTimeRefresh == null)
			minTimeRefresh = new SettingHelper(-1, "locationUpdateMinTime", "4000");
		final SettingHelper finalMinTimeRefresh = minTimeRefresh;
		minTimeRefresh.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				editTextMinTimeRefresh.setText(value);
			}
		});
		editTextMinTimeRefresh.setText(minTimeRefresh.getValue());
		layoutMinTimeRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popup.showEditSettingValue(SettingsActivity.this, finalMinTimeRefresh);
			}
		});

		SettingHelper minDistanceRefresh = SettingHelper.getSetting("locationUpdateMinDistance");
		final TextView editTextMinDistanceRefresh =
						(TextView) findViewById(R.id.textViewMinDistanceRefresh);
		final LinearLayout layoutMinDistanceRefresh =
						(LinearLayout) findViewById(R.id.layoutMinDistanceRefresh);
		if (minDistanceRefresh == null)
			minDistanceRefresh = new SettingHelper(-1, "locationUpdateMinDistance", "0");
		final SettingHelper finalMinDistanceRefresh = minDistanceRefresh;
		minDistanceRefresh.addValueChangeListener(new SettingHelper.ValueChangeListener() {
			@Override
			public void onValueChange(String value) {
				editTextMinDistanceRefresh.setText(value);
			}
		});
		editTextMinDistanceRefresh.setText(minDistanceRefresh.getValue());
		layoutMinDistanceRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popup.showEditSettingValue(SettingsActivity.this, finalMinDistanceRefresh);
			}
		});
	}

	@Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        this.finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }
}
