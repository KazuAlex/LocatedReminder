package fr.kazutoshi.locatedreminder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import fr.kazutoshi.locatedreminder.models.AlarmHelper;

/**
 * Created by Alex on 18/12/2015.
 */
public class AlarmView extends LinearLayout {

	private AlarmHelper alarm;
	private Switch toggleEnabled;
	private Button remove;
	private TextView title;
	private TextView infos;

	public AlarmView(Context context, final AlarmHelper alarm) {
		super(context);

		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		setWeightSum(10);

		this.alarm = alarm;

		LayoutParams params;

		toggleEnabled = new Switch(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		toggleEnabled.setLayoutParams(params);
		toggleEnabled.setChecked(alarm.isEnabled());
		toggleEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				alarm.setEnabled(isChecked);
			}
		});


		LinearLayout layout = new LinearLayout(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.weight = 8;
		layout.setLayoutParams(params);
		layout.setOrientation(VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);

		title = new TextView(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		title.setLayoutParams(params);
		title.setText(alarm.getName());
		title.setTextSize(20);

		infos = new TextView(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		infos.setLayoutParams(params);
		String infosText = alarm.getLocationX() + "\n" +
						alarm.getLocationY() + "\n" +
						alarm.getRadius() + "m";
		infos.setText(infosText);

		layout.addView(title);
		layout.addView(infos);


		remove = new Button(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		remove.setLayoutParams(params);
		remove.setText("X");
		remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("locatedreminder", "remove alarm");
				alarm.delete();
			}
		});

		addView(toggleEnabled);
		addView(layout);
		addView(remove);

		alarm.addEnabledListener(new AlarmHelper.EnabledListener() {
			@Override
			public void onEnabledChange(boolean enabled) {
				toggleEnabled.setChecked(enabled);
			}
		});
		alarm.addRemovedListener(new AlarmHelper.RemoveListener() {
			@Override
			public void onRemove() {
				ViewGroup parent = (ViewGroup) getParent();
				if (parent != null)
					parent.removeView(AlarmView.this);
			}
		});
	}
}
