package fr.kazutoshi.locatedreminder.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import fr.kazutoshi.locatedreminder.R;
import fr.kazutoshi.locatedreminder.models.AlarmHelper;

/**
 * Created by Alex on 18/12/2015.
 */
public class AlarmView extends RelativeLayout {

	private AlarmHelper alarm;
	private Switch toggleEnabled;
	private ImageView remove;
	private TextView title;
	private TextView infos;

	public AlarmView(Context context, final AlarmHelper alarm) {
		super(context);

		setGravity(Gravity.CENTER_VERTICAL);

		this.alarm = alarm;

		LayoutParams params;

		toggleEnabled = new Switch(context);
		toggleEnabled.setId(View.generateViewId());
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(ALIGN_PARENT_START);
		params.addRule(CENTER_VERTICAL);
		toggleEnabled.setLayoutParams(params);
		toggleEnabled.setChecked(alarm.isEnabled());
		toggleEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				alarm.setEnabled(isChecked).save();
			}
		});


		LinearLayout layoutRemove = new LinearLayout(context);
		layoutRemove.setId(View.generateViewId());
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(ALIGN_PARENT_END);
		params.addRule(CENTER_VERTICAL);
		layoutRemove.setLayoutParams(params);

		remove = new ImageView(context);
		params = new LayoutParams(50, 50);
		remove.setLayoutParams(params);
		remove.setImageResource(R.drawable.ic_delete_black_24dp);
		remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alarm.delete();
			}
		});
		layoutRemove.addView(remove);


		LinearLayout layoutInfos = new LinearLayout(context);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RIGHT_OF, toggleEnabled.getId());
		params.addRule(LEFT_OF, layoutRemove.getId());
		params.addRule(CENTER_VERTICAL);
		layoutInfos.setLayoutParams(params);
		layoutInfos.setOrientation(LinearLayout.VERTICAL);
		layoutInfos.setGravity(Gravity.CENTER_HORIZONTAL);

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

		layoutInfos.addView(title);
		layoutInfos.addView(infos);

		addView(toggleEnabled);
		addView(layoutInfos);
		addView(layoutRemove);

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
