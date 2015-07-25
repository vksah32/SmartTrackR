package com.vivek.snapshary;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class WelcomeActivity  extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (haveWifiConnection()) {
            buildGoogleApiClient();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Connection failed!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void agree(View v){
        sendRequest();

    }



    public void classifyintoTent(Double lat, Double lon){

    }


    public void sendRequest(){
//        Toast.makeText(getApplicationContext(), "lalalal", Toast.LENGTH_SHORT).show();
        if (mConnected) {
            Log.i("SENDING REQUEST", "kakak");
            Double lat = mLastLocation.getLatitude();
            Double lon = mLastLocation.getLongitude();
            MainActivity.mUser.add("CurrentLocation", lat);
            MainActivity.mUser.add("CurrentLocation", lon);
            MainActivity.mUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        classifyintoTent(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        Toast.makeText(getApplicationContext(), "" + mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            Toast.makeText(getApplicationContext(), "" + "connection failed", Toast.LENGTH_SHORT).show();
        }



    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.i("LOCATION", "got it");
        mConnected = true;
        createLocationRequest();
        if (mLocationRequest != null)
            startLocationUpdates();



    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection failed!!", Toast.LENGTH_SHORT).show();



    }

    private boolean haveWifiConnection() {
        boolean haveConnectedWifi = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
        }
        return haveConnectedWifi;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
//        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates() {
        Toast.makeText(this,"STARTED LOC UPDATE", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOC", "location changed");

        MainActivity.mUser.remove("CurrentLocation");
        MainActivity.mUser.add("CurrentLocation", location.getLatitude());
        MainActivity.mUser.add("CurrentLocation", location.getLongitude());
        MainActivity.mUser.saveInBackground();
        classifyintoTent(location.getLatitude(), location.getLongitude());


        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
        createLocationRequest();
        if (mLocationRequest != null)
            startLocationUpdates();


    }


}
