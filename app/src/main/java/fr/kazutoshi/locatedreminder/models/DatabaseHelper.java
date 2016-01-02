package fr.kazutoshi.locatedreminder.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alex on 13/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

  protected static DatabaseHelper sInstance;

  static final String dbName = "locatedReminder";

  static final String idField = "id";
  static final String createdAtField = "created_at";
  static final String updatedAtField = "updated_at";
  static final String deletedAtField = "deleted_at";

  static final String alarmsTable = "alarms";
  static final String alarmName = "name";
  static final String alarmLocationX = "location_x";
  static final String alarmLocationY = "location_y";
  static final String alarmRadius = "radius";
  static final String alarmDate = "date";
  static final String alarmEnabled = "enabled";

  static final String alarmSettingsTable = "alarm_settings";
	static final String alarmSettingsAlarmId = "alarmId";
  static final String alarmSettingsIsIn = "alarmIsIn";
  static final String alarmSettingsIsNotification = "alarmSettingsIsNotification";
	static final String alarmSettingsVibrationRepeatCount = "alarmSettingsVibrationRepeatCount";
	static final String alarmSettingsVibrationLength = "alarmSettingsVibrationLength";
  static final String alarmSettingsIsSMS = "alarmSettingsIsSMS";
  static final String alarmSettingsSMSContacts = "alarmSettingsSMSContacts";
	static final String alarmSettingsSMSContent = "alarmSettingsSMSContent";

  static final String settingsTable = "settings";
  static final String settingName = "name";
  static final String settingValue = "value";

  public static synchronized DatabaseHelper getInstance() {
      return sInstance;
  }

  public static synchronized DatabaseHelper getInstance(Context context) {
    if (sInstance == null)
      sInstance = new DatabaseHelper(context.getApplicationContext());
    return sInstance;
  }

  public static synchronized DatabaseHelper getInstance(Context context, boolean deleteDatabase) {
    if (deleteDatabase) {
      if (context.getApplicationContext() != null)
        sInstance = new DatabaseHelper(context.getApplicationContext(), true);
      else
        sInstance = new DatabaseHelper(context, true);
    } else if (sInstance == null && context != null) {
      if (context.getApplicationContext() != null)
        sInstance = new DatabaseHelper(context.getApplicationContext(), false);
      else
        sInstance = new DatabaseHelper(context, false);
    }
    return sInstance;
  }

  public DatabaseHelper(Context context) {
      this(context, false);
  }

  public DatabaseHelper(Context context, boolean deleteDatabse) {
    super(context, dbName, null, 33);

    if (deleteDatabse) {
      onUpgrade(getWritableDatabase(), 0, 1);
      onCreate(getWritableDatabase());
    }
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTableAlarms = "CREATE TABLE IF NOT EXISTS " + alarmsTable + " (" +
		        idField + " INTEGER PRIMARY KEY, " +
		        createdAtField + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
		        updatedAtField + " DATETIME, " +
		        deletedAtField + " DATETIME, " +
		        alarmName + " VARCHAR(255), " +
		        alarmLocationX + " INTEGER, " +
		        alarmLocationY + " INTEGER, " +
		        alarmRadius + " INTEGER, " +
		        alarmDate + " DATETIME, " +
		        alarmEnabled + " INTEGER);";

	  String createTableAlarmSettings = "CREATE TABLE IF NOT EXISTS " + alarmSettingsTable + " (" +
					  idField + " INTEGER PRIMARY KEY, " +
					  createdAtField + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
					  updatedAtField + " DATETIME, " +
					  deletedAtField + " DATETIME, " +
					  alarmSettingsAlarmId + " INTEGER, " +
            alarmSettingsIsIn + " INTEGER, " +
					  alarmSettingsIsNotification + " INTEGER, " +
					  alarmSettingsVibrationLength + " INTEGER, " +
					  alarmSettingsVibrationRepeatCount + " INTEGER, " +
					  alarmSettingsIsSMS + " INTEGER, " +
					  alarmSettingsSMSContacts + " TEXT, " +
					  alarmSettingsSMSContent + " TEXT);";

    String createTableSettings = "CREATE TABLE IF NOT EXISTS " + settingsTable + " (" +
	          idField + " INTEGER PRIMARY KEY, " +
	          createdAtField + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
	          updatedAtField + " DATETIME, " +
	          deletedAtField + " DATETIME, " +
	          settingName + " VARCHAR(255), " +
	          settingValue + " VARCHAR(255));";

    try {
	    db.execSQL(createTableAlarms);
	    db.execSQL(createTableAlarmSettings);
      db.execSQL(createTableSettings);
    } catch (Exception e) {
      Log.d("LocatedReminderLog", e.toString());
      for (StackTraceElement ste : e.getStackTrace())
        Log.d("LocatedReminderLog", ste.toString());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    try {
      db.execSQL("DROP TABLE IF EXISTS " + alarmsTable);
      db = getWritableDatabase();
      db.execSQL("DROP TABLE IF EXISTS " + settingsTable);
    } catch (Exception e) {
      Log.d("LocatedReminderLog", e.toString());
      for (StackTraceElement ste : e.getStackTrace())
        Log.d("LocatedReminderLog", ste.toString());
    }
  }
}
