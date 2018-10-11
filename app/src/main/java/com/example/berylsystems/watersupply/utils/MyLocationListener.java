package com.example.berylsystems.watersupply.utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.berylsystems.watersupply.bean.UserBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;

/**
 * Created by BerylSystems on 29-May-18.
 */

public class MyLocationListener implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    public Location location;
    Context context;
    boolean on;
    ProgressDialog progressDialog;

    public MyLocationListener(Context context){
        on=true;
        this.context=context;
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        mLocationRequest.setPriority(priority);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);

        mLocationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationClient.connect();
    }


    public MyLocationListener(Context context, ProgressDialog progressDialog){
        on=true;
        this.progressDialog=progressDialog;
        this.context=context;
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        mLocationRequest.setPriority(priority);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);

        mLocationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationClient.connect();
    }
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        } catch (Exception e) {

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){
            if (progressDialog!=null){
                progressDialog.dismiss();
            }
            this.location=location;
            ParameterConstants.location=location;
            new MyAsyncTask().execute();
            if (on){
                on=false;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
        }
    }


    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String address=Helper.getCompleteAddressString(context,location.getLatitude(),location.getLongitude());
            ParameterConstants.ADDRESS=address.trim();
            return null;
        }


        @Override
        protected void onPostExecute(String data) {
        }
    }

}
