package com.example.berylsystems.watersupply.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.berylsystems.watersupply.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by BerylSystems on 23-Aug-18.
 */

public class Helper {
    public static void initActionbar(Activity context, ActionBar actionBar, String title, boolean homeIcon) {
        View viewActionBar = context.getLayoutInflater().inflate(R.layout.toolbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)));
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(viewActionBar, params);
        TextView actionbarTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        actionbarTitle.setText(title);
        actionbarTitle.setTextSize(16);
        actionbarTitle.setTypeface(TypefaceCache.get(context.getAssets(), 3));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(homeIcon);
        actionBar.setHomeButtonEnabled(true);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBar(Activity context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                context.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        } catch (Error e) {
        }

    }


    public static String getTimeDifferent(String start, String end) {
        String diff = "0.0";
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh.mm aa");
            Date date1 = format.parse(end);
            Date date2 = format.parse(start);
            long mills = date1.getTime() - date2.getTime();
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;
            if (hours<0&&mins<0){
                diff=hours+"."+(-mins);
            }
            else if (hours==0&&mins<0){
                diff=hours+"."+(-mins);
            }
            else {
                diff = hours + "." + mins;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }


    void checkTime(String date) throws ParseException {
        if (new SimpleDateFormat("hh:mm aa").parse(date).before(new Date())) {

        }
    }

    public static List<String> deliverTimeList(double d){
        String min = ".30 min";
        String hour = ".00 hour";
        List<String> list = new ArrayList<>();
        list.add("30 min");
        list.add("1"+hour);
        int k = 1;
        for (int i=2;i<2*d;i++){
            if (i%2!=0){
                list.add((i-k)+hour);
                k++;
            }else {
                list.add(i/2+min);
            }
        }
        return list;
    }

    public static void openKeyPad(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void closeKeyPad(Activity activity,boolean isKeyPadOpen) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isKeyPadOpen){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static String getDayFromDateString(String stringDate) {
        String[] daysArray = new String[] {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        String day = "";

        int dayOfWeek =0;
        //dateTimeFormat = yyyy-MM-dd HH:mm:ss
        SimpleDateFormat formatter=new SimpleDateFormat("dd MMM yyyy");
//        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
        Date date;
        try {
            date = formatter.parse(stringDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;
            if (dayOfWeek < 0) {
                dayOfWeek += 7;
            }
            day = daysArray[dayOfWeek];
        }catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

}
