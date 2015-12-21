package fr.kazutoshi.locatedreminder.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by Alex on 13/12/2015.
 */
public class AlarmHelper extends GlobalHelper {

	public interface EnabledListener {
		void onEnabledChange(boolean enabled);
	}

	public interface RemoveListener {
		void onRemove();
	}

	private static HashSet<AlarmHelper> alarmInstances = new HashSet<>();

  private String name;
  private double locationX;
  private double locationY;
  private double radius;
  private Date date;
  private boolean on;
  private AlarmSettingHelper settings;
  private HashSet<EnabledListener> enabledListeners;
  private HashSet<RemoveListener> removedListeners;


  public AlarmHelper(long id, String name, double locationX, double locationY, double radius, int on) {
    this(id, name, locationX, locationY, radius, on == 1);
  }

  public AlarmHelper(long id, String name, double locationX, double locationY, double radius, boolean on) {
    setId(id);
    this.name = name;
    this.locationX = locationX;
    this.locationY = locationY;
    this.radius = radius;
    this.on = on;

	  this.settings = AlarmSettingHelper.getFromAlarmId(getId());
	  if (settings == null)
		  settings = new AlarmSettingHelper(-1, getId(), 1, 1);

	  enabledListeners = new HashSet<>();
	  removedListeners = new HashSet<>();

    alarmInstances.add(this);
  }


  /** GETTERS */
  public String getName() {
    if (name.length() == 0)
	    return "not named alarm";
    return name;
  }

  public String getRawName() {
    return name;
  }

  public double getLocationX() {
      return locationX;
  }

  public double getLocationY() {
      return locationY;
  }

  public double getRadius() {
      return radius;
  }

  public Date getDate() {
      return date;
  }

  public boolean isOn() {
      return on;
  }

  public boolean isOff() {
      return !on;
  }

	public boolean isEnabled() {
		return on;
	}

  public boolean isLocated() {
      return date == null;
  }

  public boolean isTimed() {
      return date != null;
  }

	public int getVibrationLength() {
		return settings.getVibrationLength();
	}

	public int getVibrationRepeatCount() {
		return settings.getVibrationRepeatCount();
	}



  /** SETTERS */
  public AlarmHelper setName(String name) {
    this.name = name;
    return this;
  }

  public AlarmHelper setLocationX(double locationX) {
    this.locationX = locationX;
    return this;
  }

  public AlarmHelper setLocationY(double locationY) {
    this.locationY = locationY;
    return this;
  }

  public AlarmHelper setRadius(double radius) {
    this.radius = radius;
    return this;
  }

  public AlarmHelper setDate(Date date) {
    this.date = date;
    return this;
  }

  public AlarmHelper on() {
	  setEnabled(true);
	  return this;
  }

  public AlarmHelper off() {
    setEnabled(false);
    return this;
  }

	public AlarmHelper toggle() {
		setEnabled(!on);
		return this;
	}

  public AlarmHelper setEnabled(boolean on) {
    this.on = on;
	  for (EnabledListener listener : enabledListeners)
	    listener.onEnabledChange(on);
    return this;
  }

	public AlarmHelper setVibrationLength(int vibrationLength) {
		settings.setVibrationLength(vibrationLength);
		return this;
	}

	public AlarmHelper setVibrationRepeatCount(int vibrationRepeatCount) {
		settings.setVibrationRepeatCount(vibrationRepeatCount);
		return this;
	}



	/* LISTENERS */
	public AlarmHelper addEnabledListener(EnabledListener listener) {
		if (listener != null)
			enabledListeners.add(listener);
		return this;
	}

	public AlarmHelper addRemovedListener(RemoveListener listener) {
		if (listener != null)
			removedListeners.add(listener);
		return this;
	}



  public static AlarmHelper getFromId(long id) {
    if (id <= 0)
      return null;

    if (alarmInstances.size() == 0)
      loadAlarms();

    for (AlarmHelper alarm : alarmInstances)
      if (alarm.getId() == id)
        return alarm;

    return null;
  }

	public static void loadAlarms() {
      Cursor cur = dbHelper.getReadableDatabase().rawQuery(
                      "SELECT " + DatabaseHelper.idField + " , " + DatabaseHelper.alarmName + ", " +
                                      DatabaseHelper.alarmLocationX + ", " + DatabaseHelper.alarmLocationY + ", " +
                                      DatabaseHelper.alarmRadius + ", " + DatabaseHelper.alarmEnabled +
                                      " FROM " + DatabaseHelper.alarmsTable +
                                      " WHERE " + DatabaseHelper.deletedAtField + " IS NULL",
                      new String[] {});

      if (cur == null)
          return;

      while (cur.moveToNext()) {
          new AlarmHelper(cur.getLong(cur.getColumnIndex(DatabaseHelper.idField)),
                      cur.getString(cur.getColumnIndex(DatabaseHelper.alarmName)),
                      cur.getDouble(cur.getColumnIndex(DatabaseHelper.alarmLocationX)),
                      cur.getDouble(cur.getColumnIndex(DatabaseHelper.alarmLocationY)),
                      cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmRadius)),
                      cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmEnabled)));
      }

      cur.close();
	}

  public static HashSet<AlarmHelper> getAllAlarms() {
    if (alarmInstances.size() == 0)
      loadAlarms();

    return alarmInstances;
  }


  public AlarmHelper save() {
    if (getId() < 0) {
      ContentValues values = new ContentValues();
      values.put(DatabaseHelper.createdAtField,
              getCreatedAt() == null ? null : getCreatedAt().toString());
      values.put(DatabaseHelper.updatedAtField,
              getUpdatedAt() == null ? null : getUpdatedAt().toString());
      values.put(DatabaseHelper.deletedAtField,
              getDeletedAt() == null ? null : getDeletedAt().toString());
      values.put(DatabaseHelper.alarmName, getName());
      values.put(DatabaseHelper.alarmLocationX, getLocationX());
      values.put(DatabaseHelper.alarmLocationY, getLocationY());
      values.put(DatabaseHelper.alarmRadius, getRadius());
      values.put(DatabaseHelper.alarmDate, getDate() == null ? null : getDate().toString());
      values.put(DatabaseHelper.alarmEnabled, isEnabled() ? "1" : "0");
      setId(dbHelper.getWritableDatabase().insert(DatabaseHelper.alarmsTable, null, values));
	    settings.save();
    } else if (getId() > 0) {
      ContentValues values = new ContentValues();
      values.put(DatabaseHelper.alarmName, getName());
      values.put(DatabaseHelper.alarmLocationX, getLocationX());
      values.put(DatabaseHelper.alarmLocationY, getLocationY());
      values.put(DatabaseHelper.alarmRadius, getRadius());
      values.put(DatabaseHelper.alarmDate, getDate() == null ? null : getDate().toString());
      values.put(DatabaseHelper.alarmEnabled, isEnabled() ? "1" : "0");
      dbHelper.getWritableDatabase().update(DatabaseHelper.alarmsTable, values,
				      DatabaseHelper.idField + " = ?", new String[]{String.valueOf(getId())});
	    settings.save();
    }
    return this;
  }

  public void delete() {
    if (getId() > 0) {
	    alarmInstances.remove(this);
	    dbHelper.getWritableDatabase().delete(DatabaseHelper.alarmsTable,
					    DatabaseHelper.idField + " = ?", new String[]{String.valueOf(getId())});
	    settings.delete();
	    for (RemoveListener listener : removedListeners)
		    listener.onRemove();
    }
  }
}
