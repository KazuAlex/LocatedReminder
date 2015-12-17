package fr.kazutoshi.locatedreminder.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alex on 13/12/2015.
 */
public class AlarmHelper extends GlobalHelper {

    private double locationX;
    private double locationY;
    private double radius;
    private Date date;
    private boolean on;


    public AlarmHelper(long id, double locationX, double locationY, double radius, int on) {
        this(id, locationX, locationY, radius, on == 1);
    }

    public AlarmHelper(long id, double locationX, double locationY, double radius, boolean on) {
        setId(id);
        this.locationX = locationX;
        this.locationY = locationY;
        this.radius = radius;
        this.on = on;
    }


    /** GETTERS */
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



    /** SETTERS */

    public AlarmHelper setLocationX(int locationX) {
        this.locationX = locationX;
        return this;
    }

    public AlarmHelper setLocationY(int locationY) {
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
        this.on = true;
        return this;
    }

    public AlarmHelper off() {
        this.on = false;
        return this;
    }

		public AlarmHelper toggle() {
			on = !on;
			return this;
		}

    public AlarmHelper setEnabled(boolean on) {
        this.on = on;
        return this;
    }


    public static ArrayList<AlarmHelper> getAllAlarms() {
        ArrayList<AlarmHelper> alarms = new ArrayList<>();

        Cursor cur = dbHelper.getReadableDatabase().rawQuery("SELECT " + DatabaseHelper.idField + " , " +
                DatabaseHelper.alarmLocationX + ", " + DatabaseHelper.alarmLocationY + ", " +
                DatabaseHelper.alarmRadius + ", " + DatabaseHelper.alarmEnabled +
                " FROM " + DatabaseHelper.alarmsTable +
                " WHERE " + DatabaseHelper.deletedAtField + " IS NULL",
                new String[] {});

        if (cur == null)
            return alarms;

        while (cur.moveToNext()) {
            alarms.add(new AlarmHelper(cur.getLong(cur.getColumnIndex(DatabaseHelper.idField)),
                    cur.getDouble(cur.getColumnIndex(DatabaseHelper.alarmLocationX)),
                    cur.getDouble(cur.getColumnIndex(DatabaseHelper.alarmLocationY)),
                    cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmRadius)),
                    cur.getInt(cur.getColumnIndex(DatabaseHelper.alarmEnabled))));
        }

        cur.close();

        return alarms;
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
            values.put(DatabaseHelper.alarmLocationX, getLocationX());
            values.put(DatabaseHelper.alarmLocationY, getLocationY());
            values.put(DatabaseHelper.alarmRadius, getRadius());
            values.put(DatabaseHelper.alarmDate, getDate() == null ? null : getDate().toString());
            setId(dbHelper.getWritableDatabase().insert(DatabaseHelper.alarmsTable, null, values));
        } else if (getId() > 0) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.alarmLocationX, getLocationX());
            values.put(DatabaseHelper.alarmLocationY, getLocationY());
            values.put(DatabaseHelper.alarmRadius, getRadius());
            values.put(DatabaseHelper.alarmDate, getDate() == null ? null : getDate().toString());
            dbHelper.getWritableDatabase().update(DatabaseHelper.alarmsTable, values,
                    DatabaseHelper.idField + " = ?", new String[] { String.valueOf( getId() ) });
        }
        return this;
    }

    public void delete() {
        if (getId() > 0)
            dbHelper.getWritableDatabase().delete(DatabaseHelper.alarmsTable,
                    DatabaseHelper.idField + " = ?", new String[] { String.valueOf(getId()) });
    }
}
