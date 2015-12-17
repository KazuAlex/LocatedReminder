package fr.kazutoshi.locatedreminder.models;

import android.content.ContentValues;

/**
 * Created by Alex on 13/12/2015.
 */
public class SettingHelper extends GlobalHelper {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public SettingHelper setValue(String value) {
        this.value = value;
        return this;
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
