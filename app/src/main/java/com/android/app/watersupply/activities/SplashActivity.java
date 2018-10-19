package com.android.app.watersupply.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.watersupply.R;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.MyLocationListener;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends Activity {

    @Bind(R.id.mainLayout)
    RelativeLayout mainLayout;
    @Bind(R.id.textView)
    TextView textView;
    AppUser appUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    final int PERMISSIONS_BUZZ_REQUEST = 0xABC;
    String s=" Water supply for you ";
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        new MyLocationListener(getApplicationContext());
        appUser = LocalRepositories.getAppUser(this);
        if (appUser == null) {
            appUser = new AppUser();
            LocalRepositories.saveAppUser(this, appUser);
        }
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion <= 22) {
            handler(s.charAt(i));
        }else {
            checkPermissions();
        }
    }
    void handler(char c) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i < s.length()-1) {
                    textView.append(""+c);
                    i = i + 1;
                    handler(s.charAt(i));
                } else {
                    if (appUser.user ==null){
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    }else {
                        if (appUser.user.getUserType().contains(ParameterConstants.SUPPLIER)){
                            startActivity(new Intent(getApplicationContext(), SupplierHomeActivity.class));
                        }else {
                            startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
                        }
                    }
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                    finish();
                }
            }
        }, 200);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReference("version");
//        databaseReference.child("version").addValueEventListener(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        final String version = dataSnapshot.getValue(String.class);
//                        PackageInfo packageInfo = null;
//                        try {
//                            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        } catch (PackageManager.NameNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        String versionName = packageInfo.versionName;
//                        if (!version.equals(versionName)) {
//                            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SplashActivity.this);
//                            alert.setCancelable(false);
//                            alert.setMessage("A new version of Water Supply is available.Please update to version " + version + " now.");
//                            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
//                                    i.setData(Uri.parse("https://play.google.com/store/apps"));
//                                    startActivity(i);
//                                }
//                            });
//                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    finish();
//                                }
//                            });
//                            alert.show();
//                        } else {
//                            if (appUser.user==null){
//                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
//                                finish();
//                            }else {
//                                startActivity(new Intent(getApplicationContext(), CustomerHomeActivity.class));
//                                finish();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        //handle databaseError
//                    }
//                });
//    }



    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_BUZZ_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSIONS_BUZZ_REQUEST:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler(s.charAt(i));
                        }
                    }, 1000);

                } else {
                    try {
                        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(SplashActivity.this);
                        myAlertDialog.setTitle(getString(R.string.msg_perms_needed));
                        myAlertDialog.setPositiveButton(getString(R.string.btn_quit), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        });

                        myAlertDialog.setNegativeButton(getString(R.string.btn_accept),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        checkPermissions();
                                    }
                                });
                        myAlertDialog.show();
                    }catch (Exception e){
                        Toast.makeText(this, ""+getString(R.string.msg_perms_needed), Toast.LENGTH_SHORT).show();
                        System.out.println("AAAAAAAAA "+e.getMessage());
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
