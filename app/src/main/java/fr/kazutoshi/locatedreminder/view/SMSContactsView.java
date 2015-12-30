package fr.kazutoshi.locatedreminder.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import fr.kazutoshi.locatedreminder.R;

/**
 * Created by alex on 12/28/15.
 */
public class SMSContactsView extends FlowLayout {

  ArrayMap<String, View> contactsView = new ArrayMap<>();
	ArrayMap<String, String> contactsName = new ArrayMap<>();

	public SMSContactsView(Context context) {
		super(context);

		init();
	}

	public SMSContactsView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public SMSContactsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	private void init() {
		inflate(getContext(), R.layout.smscontactsview, this);

		setOrientation(HORIZONTAL);
	}

	private boolean resolveContact(final Uri data) {
		try {
			Cursor cursor = getContext().getContentResolver().query(data, null, null, null, null);

			if (cursor != null)
				if (cursor.getCount() != 0) {
					while (cursor.moveToNext())
						addContact(
										cursor.getString(cursor.getColumnIndex(
														ContactsContract.CommonDataKinds.Phone.NUMBER)),
										cursor.getString(cursor.getColumnIndex(
														ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

				cursor.close();

				return true;
			}
		} catch (SecurityException e) {
			Log.e("locatedreminder", "catch error : " + e.toString());
		}

		return false;
	}

	public SMSContactsView addContacts(String contacts) {
		if (!contacts.isEmpty()) {
			String[] contactsArray = contacts.split(";");
			for (String contact : contactsArray) {
				Log.d("locatedreminder", "contact : " + contact);
				addContact(contact);
			}
		}
		return this;
	}

	public SMSContactsView addContact(final Uri data) {
		resolveContact(data);

		return this;
	}

	public SMSContactsView addContact(final String number) {
		Uri uri = Uri.withAppendedPath(
						ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(number));

		if (!resolveContact(uri)) {
			addContact(number, number);
		}

		return this;
	}

  public SMSContactsView addContact(final String number, final String name) {
    if (!contactsView.containsKey(number)) {
	    LinearLayout layout = new LinearLayout(getContext());
	    layout.setPadding(20, 20, 20, 20);
	    layout.setOrientation(LinearLayout.HORIZONTAL);
	    layout.setGravity(Gravity.CENTER_VERTICAL);

	    /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    params.setMargins(30, 30, 30, 30);
	    layout.setLayoutParams(params);*/

	    TextView view = new TextView(getContext());
	    view.setPadding(20, 20, 20, 20);
	    view.setText(name);
	    view.setBackgroundColor(Color.parseColor("#6ba0f6"));
	    view.setTextColor(Color.WHITE);

	    TextView viewRemove = new TextView(getContext());
	    viewRemove.setPadding(30, 20, 20, 20);
	    viewRemove.setText("x");
	    viewRemove.setBackgroundColor(Color.parseColor("#6ba0f6"));
	    viewRemove.setTextColor(Color.BLACK);
	    viewRemove.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    removeContact(number);
		    }
	    });

	    layout.addView(view);
	    layout.addView(viewRemove);

      contactsView.put(number, layout);
	    contactsName.put(number, name);
	    addView(layout);
      invalidate();
    }

    return this;
  }

	public ArrayList<String> getAllNumbers() {
		return new ArrayList<>(contactsName.keySet());
	}

	public String getAllNumbersString() {
		ArrayList<String> contacts = getAllNumbers();
		String contactsString = "";
		for (String contact : contacts) {
			if (!contactsString.isEmpty())
				contactsString += ";";
			contactsString += contact;
		}
		return contactsString;
	}

	public SMSContactsView removeContact(String number) {
		if (contactsView.containsKey(number)) {
			removeView(contactsView.get(number));
			contactsView.remove(number);
			contactsName.remove(number);
			invalidate();
		}

		return this;
	}

	public SMSContactsView clear() {
		contactsView.clear();
		contactsName.clear();
		super.removeAllViews();
		invalidate();
		return this;
	}
}
