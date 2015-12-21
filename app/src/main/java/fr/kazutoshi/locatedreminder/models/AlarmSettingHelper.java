package fr.kazutoshi.locatedreminder.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashSet;

/**
 * Created by Alex on 21/12/2015.
 */
public class AlarmSettingHelper extends GlobalHelper {

	private static HashSet<AlarmSettingHelper> alarmSettingInstances = new HashSet<>();

	private long alarmId;
	private int vibrationLength;
	private int vibrationRepeatCount;

	public AlarmSettingHelper(long id, long alarmId, int vibrationLength, int vibrationRepeatCount) {
		setId(id);
		this.alarmId = alarmId;
		this.vibrationLength = vibrationLength;
		this.vibrationRepeatCount = vibrationRepeatCount;

		alarmSettingInstances.add(this);
	}


	/** GETTERS */
	public long getAlarmId() {
		return alarmId;
	}

	public int getVibrationLength() {
		return vibrationLength;
	}

	public int getVibrationRepeatCount() {
		return vibrationRepeatCount;
	}


	/** SETTERS */
	public AlarmSettingHelper setVibrationLength(int vibrationLength) {
		this.vibrationLength = vibrationLength;
		return this;
	}

	public AlarmSettingHelper setVibrationRepeatCount(int vibrationRepeatCount) {
		this.vibrationRepeatCount = vibrationRepeatCount;
		return this;
	}



	public static AlarmSettingHelper getFromAlarmId(long id) {
		if (id <= 0)
			return null;

		Cursor cur = dbHelper.getReadableDatabase().rawQuery(
						"SELECT " + DatabaseHelper.idField + ", " +
						DatabaseHelper.alarmSettingsVibrationLength + ", " +
						DatabaseHelper.alarmSettingsVibrationRepeatCount +
						" FROM " + DatabaseHelper.alarmSettingsTable +
						" WHERE " + DatabaseHelper.deletedAtField + " IS NULL" +
						" AND " + DatabaseHelper.alarmSettingsAlarmId + " = ?",
						new String[] { String.valueOf(id) });

		if (cur == null)
			return null;

		if (!cur.moveToFirst()) {
			cur.close();
			return null;
		}

		AlarmSettingHelper settings = new AlarmSettingHelper(
						cur.getLong(cur.getColumnIndex(DatabaseHelper.idField)),
						id,
						cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsVibrationLength)),
						cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsVibrationRepeatCount)));

		cur.close();
		return settings;
	}



	public AlarmSettingHelper save() {
		if (getId() < 0) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.createdAtField,
							getCreatedAt() == null ? null : getCreatedAt().toString());
			values.put(DatabaseHelper.updatedAtField,
							getUpdatedAt() == null ? null : getUpdatedAt().toString());
			values.put(DatabaseHelper.deletedAtField,
							getDeletedAt() == null ? null : getDeletedAt().toString());
			values.put(DatabaseHelper.alarmSettingsAlarmId, getAlarmId());
			values.put(DatabaseHelper.alarmSettingsVibrationLength, getVibrationLength());
			values.put(DatabaseHelper.alarmSettingsVibrationRepeatCount, getVibrationRepeatCount());
			setId(dbHelper.getWritableDatabase().insert(DatabaseHelper.alarmSettingsTable, null, values));
		} else if (getId() > 0) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.alarmSettingsVibrationLength, getVibrationLength());
			values.put(DatabaseHelper.alarmSettingsVibrationRepeatCount, getVibrationRepeatCount());
			setId(dbHelper.getWritableDatabase().update(DatabaseHelper.alarmSettingsTable, values,
							DatabaseHelper.idField + " = ?", new String[] { String.valueOf(getId()) }));
		}

		return this;
	}

	public void delete() {
		if (getId() > 0) {
			alarmSettingInstances.remove(this);
			dbHelper.getWritableDatabase().delete(DatabaseHelper.alarmSettingsTable,
							DatabaseHelper.idField + " = ?", new String[]{String.valueOf(getId())});
		}
	}
}
