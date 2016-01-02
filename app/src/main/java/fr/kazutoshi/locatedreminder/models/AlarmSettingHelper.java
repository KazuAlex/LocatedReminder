package fr.kazutoshi.locatedreminder.models;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.ArrayMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Alex on 21/12/2015.
 */
public class AlarmSettingHelper extends GlobalHelper {

	private static HashSet<AlarmSettingHelper> alarmSettingInstances = new HashSet<>();

	private long alarmId;
	private boolean isIn;
	private boolean isNotification;
	private int vibrationLength;
	private int vibrationRepeatCount;
	private boolean isSMS;
	private String SMSContacts;
	private String SMSContent;

	public AlarmSettingHelper(long id, long alarmId, boolean isIn,
	                          boolean isNotification, int vibrationLength, int vibrationRepeatCount,
	                          boolean isSMS, String SMSContacts, String SMSContent) {
		setId(id);
		this.alarmId = alarmId;
    this.isIn = isIn;
		this.isNotification = isNotification;
		this.vibrationLength = vibrationLength;
		this.vibrationRepeatCount = vibrationRepeatCount;
		this.isSMS = isSMS;
		this.SMSContacts = SMSContacts;
		if (SMSContacts == null)
			this.SMSContacts = "";
		this.SMSContent = SMSContent;
		if (SMSContent == null)
			this.SMSContent = "";

		alarmSettingInstances.add(this);
	}


	/** GETTERS */
	public long getAlarmId() {
		return alarmId;
	}

  public boolean isIn() {
    return isIn;
  }

  public boolean isOut() {
    return !isIn;
  }

	public boolean isNotification() {
		return isNotification;
	}

	public int getVibrationLength() {
		return vibrationLength;
	}

	public int getVibrationRepeatCount() {
		return vibrationRepeatCount;
	}

	public boolean isSMS() {
		return isSMS;
	}

	public String getSMSContacts() {
		return SMSContacts;
	}

	public String getSMSContent() {
		return SMSContent;
	}


	/** SETTERS */
	public AlarmSettingHelper setAlarmId(long id) {
		alarmId = id;
		return this;
	}

  public AlarmSettingHelper setIsIn(boolean isIn) {
    this.isIn = isIn;
    return this;
  }

  public AlarmSettingHelper setIn() {
    return setIsIn(true);
  }

  public AlarmSettingHelper setOut() {
    return setIsIn(false);
  }

	public AlarmSettingHelper setIsNotification(boolean isNotification) {
		this.isNotification = isNotification;
		return this;
	}

	public AlarmSettingHelper setVibrationLength(int vibrationLength) {
		this.vibrationLength = vibrationLength;
		return this;
	}

	public AlarmSettingHelper setVibrationRepeatCount(int vibrationRepeatCount) {
		this.vibrationRepeatCount = vibrationRepeatCount;
		return this;
	}

	public AlarmSettingHelper setIsSMS(boolean isSMS) {
		this.isSMS = isSMS;
		return this;
	}

	public AlarmSettingHelper setSMSContacts(String SMSContacts) {
		this.SMSContacts = SMSContacts;
		return this;
	}

	public AlarmSettingHelper setSMSContent(String SMSContent) {
		this.SMSContent = SMSContent;
		return this;
	}



	public static AlarmSettingHelper getFromAlarmId(long id) {
		if (id <= 0)
			return null;

		Cursor cur = dbHelper.getReadableDatabase().rawQuery(
						"SELECT " + DatabaseHelper.idField + ", " +
            DatabaseHelper.alarmSettingsIsIn + ", " +
						DatabaseHelper.alarmSettingsIsNotification + ", " +
						DatabaseHelper.alarmSettingsVibrationLength + ", " +
						DatabaseHelper.alarmSettingsVibrationRepeatCount + ", " +
						DatabaseHelper.alarmSettingsIsSMS + ", " +
						DatabaseHelper.alarmSettingsSMSContacts + ", " +
						DatabaseHelper.alarmSettingsSMSContent +
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
            cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsIsIn)) == 1,
						cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsIsNotification)) == 1,
						cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsVibrationLength)),
						cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsVibrationRepeatCount)),
						cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmSettingsIsSMS)) == 1,
						cur.getString(cur.getColumnIndex(DatabaseHelper.alarmSettingsSMSContacts)),
						cur.getString(cur.getColumnIndex(DatabaseHelper.alarmSettingsSMSContent)));

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
      values.put(DatabaseHelper.alarmSettingsIsIn, isIn() ? "1" : "0");
			values.put(DatabaseHelper.alarmSettingsIsNotification, isNotification() ? 1 : 0);
			values.put(DatabaseHelper.alarmSettingsVibrationLength, getVibrationLength());
			values.put(DatabaseHelper.alarmSettingsVibrationRepeatCount, getVibrationRepeatCount());
			values.put(DatabaseHelper.alarmSettingsIsSMS, isSMS() ? 1 : 0);
			values.put(DatabaseHelper.alarmSettingsSMSContacts, getSMSContacts());
			values.put(DatabaseHelper.alarmSettingsSMSContent, getSMSContent());
			setId(dbHelper.getWritableDatabase().insert(DatabaseHelper.alarmSettingsTable, null, values));
		} else if (getId() > 0) {
			ContentValues values = new ContentValues();
      values.put(DatabaseHelper.alarmSettingsIsIn, isIn() ? "1" : "0");
			values.put(DatabaseHelper.alarmSettingsIsNotification, isNotification() ? 1 : 0);
			values.put(DatabaseHelper.alarmSettingsVibrationLength, getVibrationLength());
			values.put(DatabaseHelper.alarmSettingsVibrationRepeatCount, getVibrationRepeatCount());
			values.put(DatabaseHelper.alarmSettingsIsSMS, isSMS() ? 1 : 0);
			values.put(DatabaseHelper.alarmSettingsSMSContacts, getSMSContacts());
			values.put(DatabaseHelper.alarmSettingsSMSContent, getSMSContent());
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



	private ArrayMap<String, String> convertToContactsArray(Context context, String contacts) {
		ArrayMap<String, String> contactsArray = new ArrayMap<>();

		ContentResolver resolver = context.getContentResolver();
		for(String phoneNumber : contacts.split(";")) {
			Uri uri = Uri.withAppendedPath(
							ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
			Cursor cursor = resolver.query(
							uri, new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
			if (cursor == null)
				continue;
			if (cursor.moveToFirst())
				contactsArray.put(phoneNumber,
								cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
			else
				contactsArray.put(phoneNumber, phoneNumber);
			if (!cursor.isClosed())
				cursor.close();
		}

		return contactsArray;
	}
}
