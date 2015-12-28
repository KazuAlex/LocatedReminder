package fr.kazutoshi.locatedreminder.view;

import android.content.Context;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by alex on 12/28/15.
 */
public class SMSContactsView extends FlowLayout {

  ArrayList<String> contacts;

  public SMSContactsView(Context context) {
    super(context);

    setOrientation(HORIZONTAL);
  }
}
