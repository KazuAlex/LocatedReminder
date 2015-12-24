package fr.kazutoshi.locatedreminder.models;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Alex on 13/12/2015.
 */
public class SettingHelper extends GlobalHelper {

  private String name;
  private String value;

  public SettingHelper(long id, String name, String value) {
    setId(id);
    this.name = name;
	  this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public SettingHelper setName(String name) {
    this.name = name;
    return this;
  }

  public SettingHelper setValue(String value) {
    this.value = value;
    return this;
  }



	public static SettingHelper getSetting(String name) {
		SettingHelper setting;

		Cursor cur = dbHelper.getReadableDatabase().rawQuery(
						"SELECT " + DatabaseHelper.idField + ", " +
										DatabaseHelper.settingName + ", " + DatabaseHelper.settingValue +
										" FROM " + DatabaseHelper.settingsTable +
										" WHERE " + DatabaseHelper.settingName + " = ?",
						new String[]{ name });

		if (cur == null)
			return null;

		if (!cur.moveToNext()) {
			cur.close();
			return null;
		}

		setting = new SettingHelper(cur.getLong(cur.getColumnIndex(DatabaseHelper.idField)),
						cur.getString(cur.getColumnIndex(DatabaseHelper.settingName)),
						cur.getString(cur.getColumnIndex(DatabaseHelper.settingValue)));

		cur.close();

		return setting;
	}

	public static String getSettingValue(String name) {
		SettingHelper setting = getSetting(name);

		if (setting == null)
			return null;
		return setting.getValue();
	}



  public SettingHelper save() {
    if (getId() < 0) {
      ContentValues values = new ContentValues();
      values.put(DatabaseHelper.createdAtField,
              getCreatedAt() == null ? null : getCreatedAt().toString());
      values.put(DatabaseHelper.updatedAtField,
              getUpdatedAt() == null ? null : getUpdatedAt().toString());
      values.put(DatabaseHelper.deletedAtField,
              getDeletedAt() == null ? null : getDeletedAt().toString());

      values.put(DatabaseHelper.settingName, getName());
      values.put(DatabaseHelper.settingValue, getValue());
      dbHelper.getWritableDatabase().insert(DatabaseHelper.settingsTable, null, values);
    } else if (getId() > 0) {
      ContentValues values = new ContentValues();
      values.put(DatabaseHelper.settingName, getName());
      values.put(DatabaseHelper.settingValue, getValue());
      dbHelper.getWritableDatabase().update(DatabaseHelper.settingsTable, values,
              DatabaseHelper.idField + " = ?", new String[] { String.valueOf(getId()) });
    }
    return this;
  }
}
