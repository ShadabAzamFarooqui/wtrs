package com.android.app.watersupply.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;


import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

  /*  public static MyService context;
//    private static final String TAG = MyService.class.getSimpleName();
    private static final String TAG = "http AAAAAAAAAAAA  ";
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    public Location location;
    public static final long INTERVAL = 20000;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null; // timer handling


    public static final String ACTION_LOCATION_BROADCAST = MyService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    DataBaseHandler dataBaseHandler;
    private static final int MY_NOTIFICATION_ID = 1;
    NotificationManager notificationManager;
    Notification myNotification;
    public static Location mCurrentLocation, lStart, lEnd;
    double speed;*/

    @Override
    public void onCreate() {
        // cancel if service is  already existed
       /* context = this;
        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL);// schedule task*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       /* dataBaseHandler = new DataBaseHandler(getApplicationContext());
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        mLocationRequest.setSmallestDisplacement(9);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        // onTaskRemoved(intent);
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
*/
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
            Log.d(TAG, "Connected to Google API");
        } catch (Exception e) {

        }*/

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
       /* Toast.makeText(getApplicationContext(), "Location off", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Connection suspended");*/
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
       /* Log.d(TAG, "Location changed");
        if (location != null) {
            this.location = location;
            mCurrentLocation = location;
            if (lStart == null) {
                lStart = mCurrentLocation;
                lEnd = mCurrentLocation;
            } else {
                lEnd = mCurrentLocation;
            }

                updateUI(location);
                Preferences.getInstance(getApplicationContext()).setLatitude(String.valueOf(location.getLatitude()));
                Preferences.getInstance(getApplicationContext()).setLongitude(String.valueOf(location.getLongitude()));
                Log.d(TAG, "== location != null");
                String lat = String.valueOf(location.getLatitude());
                String lng = String.valueOf(location.getLongitude());
                if (FlightModeReceiver.isFlight()) {
                    return;
                }
                if (!GpsReceiver.isGps(this)) {
                    return;
                }
        }
        if (Preferences.getInstance(this).getService().equals("1")) {
            showNotification();
        } else {
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MY_NOTIFICATION_ID);
        }*/

    }

    public String CompareTime(String strTimeToCompare) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        Calendar cal = Calendar.getInstance();
        int dtHour;
        int dtMin;
        int iAMPM;
        String strAMorPM = null;
        Date dtCurrentDate;
        try {
            Date TimeToCompare = sdf.parse(strTimeToCompare);
            dtMin = cal.get(Calendar.MINUTE);
            dtHour = cal.get(Calendar.HOUR);
            iAMPM = cal.get(Calendar.AM_PM);
            if (iAMPM == 1) {
                strAMorPM = "pm";
            }
            if (iAMPM == 0) {
                strAMorPM = "am";
            }
            dtCurrentDate = sdf.parse(dtHour + ":" + dtMin + " " + strAMorPM);
            if (dtCurrentDate.after(TimeToCompare)) {
                return "1";
            }
            if (dtCurrentDate.before(TimeToCompare)) {
                return "2";
            }
            if (dtCurrentDate.equals(TimeToCompare)) {
                return "3";
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "4";

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*Toast.makeText(getApplicationContext(), "Location off", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Failed to connect to Google API");
*/
    }




   /* private void updateUI(Location location) {
        speed = location.getSpeed() * 18 / 5;
        // Toast.makeText(getApplicationContext(),String.valueOf(lStart.distanceTo(lEnd)-lEnd.getAccuracy()),Toast.LENGTH_LONG).show();
        if (lStart.distanceTo(lEnd) - lEnd.getAccuracy() > 0 && lStart.distanceTo(lEnd) > 9) {
            Preferences.getInstance(getApplicationContext()).setDistance(String.valueOf(Double.valueOf(Preferences.getInstance(getApplicationContext()).getDistance()) + (lStart.distanceTo(lEnd) / 1000.00)));
            Main2Activity.endTime = System.currentTimeMillis();
            try {
                Main2Activity.tempDist.setText(new DecimalFormat("#.##").format(Double.valueOf(Preferences.getInstance(getApplicationContext()).getDistance())) + " Km.");
            } catch (Exception e) {
            }
            lStart = lEnd;
            Main2Activity.startTime = Main2Activity.endTime;
        }
    }
*/
    private void showNotification() {
        /*try {


           *//* Intent myIntent = new Intent(this, Main2Activity.class);
            @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    myIntent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);*//*

            myNotification = new NotificationCompat.Builder(this)
                    .setContentTitle("mTrack")
                    .setContentText(String.valueOf(new DecimalFormat("#.##").format(Double.valueOf(Preferences.getInstance(getApplicationContext()).getDistance())) + " Km."))
                    .setTicker("Notification!")
                    .setWhen(System.currentTimeMillis())
                    //  .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.mtrack_icon)
                    .build();

            myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            myNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
        } catch (Exception e) {

        }*/
    }

   /* private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (!NetWorkReceiver.isNetwork(getApplicationContext())) {

                            Preferences.getInstance(getApplicationContext()).setLatitude(String.valueOf(location.getLatitude()));
                            Preferences.getInstance(getApplicationContext()).setLongitude(String.valueOf(location.getLongitude()));
                            String arr2[] = Preferences.getInstance(getApplicationContext()).getShift().split(" - ");
                            String start = arr2[0];
                            String end = arr2[1];

                            if (CompareTime(start).equals("1")) {
                                if (CompareTime(end).equals("2")) {

                                    String startTime = dataBaseHandler.getTime();
                                    String endTime = Helpers.getCurrentTime();
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    Date dt1 = null;
                                    if (startTime.equals("")) {
                                        dataBaseHandler.insert(location.getLatitude(), location.getLongitude());
//                                        Toast.makeText(MyService.this, "" + GetJson.getResults(getApplicationContext()), Toast.LENGTH_SHORT).show();
                                        System.out.println("http insert offline off duty " + GetJson.getResults(getApplicationContext()));
                                    } else {

                                        dt1 = formatter.parse(startTime);

                                        Date dt2 = null;
                                        try {
                                            dt2 = formatter.parse(endTime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if ((dt2.getTime() > dt1.getTime())) {
                                            dataBaseHandler.insert(location.getLatitude(), location.getLongitude());
//                                            Toast.makeText(MyService.this, "" + GetJson.getResults(getApplicationContext()), Toast.LENGTH_SHORT).show();
                                            System.out.println("http insert offline on duty " + GetJson.getResults(getApplicationContext()));
                                        }

                                    }
                                } else {
                                    Preferences.getInstance(getApplicationContext()).setServicess("false");
                                }
                            } else {
                                Preferences.getInstance(getApplicationContext()).setServicess("false");
                            }
                            if (Preferences.getInstance(getApplicationContext()).getServicess().equals("false")) {
                                if (Preferences.getInstance(getApplicationContext()).getServicebol().equals("true")) {
                                    String startTime = dataBaseHandler.getTime();
                                    String endTime = Helpers.getCurrentTime();
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    Date dt1 = null;
                                    try {
                                        if (startTime.equals("")) {
                                            dataBaseHandler.insert(location.getLatitude(), location.getLongitude());
//                                            Toast.makeText(MyService.this, "" + GetJson.getResults(getApplicationContext()), Toast.LENGTH_SHORT).show();
                                            System.out.println("http insert offline off duty " + GetJson.getResults(getApplicationContext()));
                                        } else {
                                            dt1 = formatter.parse(startTime);
                                            Date dt2 = null;
                                            try {
                                                dt2 = formatter.parse(endTime);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            if (dt2.getTime() > dt1.getTime()) {
                                                dataBaseHandler.insert(location.getLatitude(), location.getLongitude());
//                                                Toast.makeText(MyService.this, "" + GetJson.getResults(getApplicationContext()), Toast.LENGTH_SHORT).show();
                                                System.out.println("http insert offline off duty " + GetJson.getResults(getApplicationContext()));
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                            return;
                        }


                        Preferences.getInstance(getApplicationContext()).setLatitude(String.valueOf(location.getLatitude()));
                        Preferences.getInstance(getApplicationContext()).setLongitude(String.valueOf(location.getLongitude()));
                        String arr[] = Preferences.getInstance(getApplicationContext()).getShift().split(" - ");
                        String start = arr[0];
                        String end = arr[1];

                        if (CompareTime(start).equals("1")) {
                            if (CompareTime(end).equals("2")) {

                                if (isScreenOn()) {
                                    System.out.println("http on");
//                                    PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
//                                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
//                                    screenLock.acquire();
//
//                                    float brightness = 100;
//                                    WindowManager.LayoutParams lp = Main2Activity.window.getAttributes();
//                                    lp.screenBrightness = brightness;
//                                    Main2Activity.window.setAttributes(lp);


                                } else {
                                    System.out.println("http off");
//                                    PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
//                                            PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
//                                    screenLock.acquire();
//
//                                    WindowManager.LayoutParams lp = Main2Activity.window.getAttributes();
//                                    lp.screenBrightness = 0;
//                                    Main2Activity.window.setAttributes(lp);

                                }

                                dataBaseHandler.insert(location.getLatitude(), location.getLongitude());
                                System.out.println("http insert online on duty " + GetJson.getResults(getApplicationContext()));
                                Preferences.getInstance(getApplicationContext()).setServicess("true");
                                ApiCallsService.action(getApplicationContext(), Cv.ACTION_SEND_LOCATION);



                               *//* PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                                screenLock.acquire();*//*
                            } else {
                                Preferences.getInstance(getApplicationContext()).setServicess("false");

                            }
                        } else {
                            Preferences.getInstance(getApplicationContext()).setServicess("false");
                        }
                        if (Preferences.getInstance(getApplicationContext()).getServicess().equals("false")) {
                            if (Preferences.getInstance(getApplicationContext()).getServicebol().equals("true")) {


                                dataBaseHandler.insert(location.getLatitude(), location.getLongitude());
                                System.out.println("http insert online off duty " + GetJson.getResults(getApplicationContext()));
                                ApiCallsService.action(getApplicationContext(), Cv.ACTION_SEND_LOCATION);


                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    }*/

   /* public void AppExit(Context context) {
        try {
            ActivityManager activityMgr =
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
        } catch (Exception e) {
        }
    }*/

   /* @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
// Wakes up the device in Doze Mode
            noti();
            myAlarmService.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000,
                    restartPendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
// Wakes up the device in Idle Mode
            myAlarmService.setExact(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000, restartPendingIntent);
        } else {
// Old APIs
            myAlarmService.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000, restartPendingIntent);
        }
*//*        myAlarmService.set(
               AlarmManager.ELAPSED_REALTIME,
               SystemClock.elapsedRealtime() + 1000,
               restartPendingIntent);*//*
        // Toast.makeText(getApplicationContext(), "Task removed", Toast.LENGTH_LONG).show();

        super.onTaskRemoved(rootIntent);
    }*/

    boolean isScreenOn() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

   /* public void noti() {
        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle("mTrack")
                        .setContentText("running")
                        .setTicker("Notification!")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.app_icon)
                        .build();

        startForeground(1, notification);
    }
*/

}