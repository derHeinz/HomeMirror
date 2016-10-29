package com.morristaedt.mirror.modules;

import android.text.Html;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by HannahMitt on 8/23/15.
 */
public class DayModule {

    public static Spanned getDay() {
        SimpleDateFormat formatDayOfMonth = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM", Locale.getDefault());
        Calendar now = Calendar.getInstance();
        int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

        return Html.fromHtml(formatDayOfMonth.format(now.getTime()) + ", der " + dayOfMonth + " " + formatMonth.format(now.getTime()));
    }
}
