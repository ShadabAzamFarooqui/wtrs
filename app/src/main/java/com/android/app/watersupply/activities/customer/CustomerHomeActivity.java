package com.android.app.watersupply.activities.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.app.watersupply.R;
import com.android.app.watersupply.activities.HistoryActivity;
import com.android.app.watersupply.activities.SignInActivity;
import com.android.app.watersupply.activities.SignUpActivity;
import com.android.app.watersupply.fragment.customer.OrderListFragment;
import com.android.app.watersupply.fragment.customer.SupplierListFragment;
import com.android.app.watersupply.utils.AppUser;
import com.android.app.watersupply.utils.Helper;
import com.android.app.watersupply.utils.LocalRepositories;
import com.android.app.watersupply.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shadab Azam Farooqui on 6/25/2018.
 */

public class CustomerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.viewpager)
    ViewPager mHeaderViewPager;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    boolean isExit;
    AppUser appUser;
    DrawerLayout drawer;
    ArrayList<Uri> arrayListapkFilepath;
    public static boolean bool = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_view_customer);
        ButterKnife.bind(this);
        appUser = LocalRepositories.getAppUser(this);
        setupViewPager(mHeaderViewPager);
        mTabLayout.setupWithViewPager(mHeaderViewPager);
        mHeaderViewPager.setOffscreenPageLimit(3);
        navigation();
    }


    void navigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView company_logo = (ImageView) header.findViewById(R.id.imageView);
        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText(appUser.user.getName());
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void myAccount(View view) {
        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ParameterConstants.isUpdate = true;
                ParameterConstants.KEY = "Customer";
                startActivity(new Intent(getApplicationContext(), CustomerProfile.class));
            }
        }, 400);

    }

    public void history(View view) {
        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HistoryActivity.userType = "Customer";
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
            }
        }, 400);
    }

    public void share(View v) {
        drawer.closeDrawers();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sent from "+getResources().getString(R.string.app_name));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.share_message));
        startActivity(Intent.createChooser(shareIntent, "Share via"));

    }

    public void send(View v) {
        drawer.closeDrawers();
        //put this code when you wants to share apk
        arrayListapkFilepath = new ArrayList<Uri>();
        shareAPK(getPackageName());
        // you can pass bundle id of installed app in your device instead of getPackageName()
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("application/vnd.android.package-archive");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                arrayListapkFilepath);
        startActivity(Intent.createChooser(intent, "Send " +"this"
                /*arrayListapkFilepath.size()*/ + " App Via"));

    }





    //Method
    public void shareAPK(String bundle_id) {
        File f1;
        File f2 = null;

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        int z = 0;
        for (Object object : pkgAppsList) {

            ResolveInfo info = (ResolveInfo) object;
            if (info.activityInfo.packageName.equals(bundle_id)) {

                f1 = new File(info.activityInfo.applicationInfo.publicSourceDir);

                Log.v("file--",
                        " " + f1.getName().toString() + "----" + info.loadLabel(getPackageManager()));
                try {

                    String file_name = info.loadLabel(getPackageManager()).toString();
                    Log.d("file_name--", " " + file_name);

                    f2 = new File(Environment.getExternalStorageDirectory().toString() + "/Folder");
                    f2.mkdirs();
                    f2 = new File(f2.getPath() + "/" + file_name + ".apk");
                    f2.createNewFile();

                    InputStream in = new FileInputStream(f1);

                    OutputStream out = new FileOutputStream(f2);

                    // byte[] buf = new byte[1024];
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    System.out.println("File copied.");
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage() + " in the specified directory.");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        arrayListapkFilepath.add(Uri.fromFile(new File(f2.getAbsolutePath())));

    }

    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OrderListFragment(), "Order");
        adapter.addFragment(new SupplierListFragment(), "Supplier");
        viewPager.setAdapter(adapter);
    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        buildAlertMessageNoGps();
        Helper.initActionbar(this, getSupportActionBar(), "Home Page", false);
//        appUser = LocalRepositories.getAppUser(this);
//        setupViewPager(mHeaderViewPager);
//        mTabLayout.setupWithViewPager(mHeaderViewPager);
//        mHeaderViewPager.setCurrentItem(1, true);

        if (!Helper.isNetworkAvailable(getApplicationContext())) {
            Snackbar.make(coordinatorLayout, "Please check your network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        if (bool) {
            bool = false;
            mHeaderViewPager.setCurrentItem(0, true);
        }
        versionCheck();
    }

    void versionCheck() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("version");
        databaseReference.child("version").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String version = dataSnapshot.getValue(String.class);
                        PackageInfo packageInfo = null;
                        try {
                            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String versionName = packageInfo.versionName;
                        if (!version.equals(versionName)) {
                            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(CustomerHomeActivity.this);
                            alert.setCancelable(false);
                            alert.setMessage("A new version of Water Supply is available.Please update to version " + version + " now.");
                            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("https://play.google.com/store/apps"));
                                    startActivity(i);
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                            alert.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (isExit) {
            super.onBackPressed();
        }
        isExit = true;
        Toast.makeText(this, "Press back again to Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            appUser.user = null;
            LocalRepositories.saveAppUser(getApplicationContext(), appUser);
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
//            overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
            finish();
        }
        if (id == R.id.clear_order) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Order");
            database.setValue(null, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
//                        PendingOrderFragment.orderBeanList.clear();
//                        PendingOrderFragment.mAdapter.notifyDataSetChanged();
//
//                        DispatchOrderFragment.orderBeanList.clear();
//                        DispatchOrderFragment.mAdapter.notifyDataSetChanged();
//
//                        DeliveredOrderFragment.orderBeanList.clear();
//                        DeliveredOrderFragment.mAdapter.notifyDataSetChanged();
//
//                        CancelOrderFragment.orderBeanList.clear();
//                        CancelOrderFragment.mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Order Cleared", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }


    private void buildAlertMessageNoGps() {
        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            @SuppressLint("RestrictedApi") final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return;
        }

    }

}
