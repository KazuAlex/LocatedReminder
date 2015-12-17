package fr.kazutoshi.locatedreminder.models;

import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alex on 13/12/2015.
 */
public class GlobalHelper {

    protected static DatabaseHelper dbHelper;

    private long id;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public static void setDatabaseHelper(DatabaseHelper databaseHelper) {
        dbHelper = databaseHelper;
    }

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    protected static Date getDate(Cursor cur, String field) {
        String dateString = cur.getString(cur.getColumnIndex(field));

        if (dateString == null)
            return new java.util.Date();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        java.util.Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            Log.e("CSPSLogs", e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                Log.e("CSPSLogs", ste.toString());
        }

        return date;
    }

    protected GlobalHelper setId(long id) {
        this.id = id;
        return this;
    }
}
