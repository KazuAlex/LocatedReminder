package fr.kazutoshi.locatedreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Alex on 15/12/2015.
 */
public class LocatedReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent detected", Toast.LENGTH_SHORT).show();
        Intent service = new Intent(context, LocatedReminderService.class);
        context.startService(service);
    }
}
