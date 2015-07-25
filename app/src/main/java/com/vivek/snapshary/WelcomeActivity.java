package com.vivek.snapshary;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class WelcomeActivity  extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected;


    private static final String LOG_TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String userID = MainActivity.getUserID(this);
        Log.i(LOG_TAG, "User ID = " + userID);

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



    public void sendRequest(){
//        Toast.makeText(getApplicationContext(), "lalalal", Toast.LENGTH_SHORT).show();
        if (mConnected) {
            Log.i("SENDING REQUEST", "kakak");
            Double lat = mLastLocation.getLatitude();
            Double Long = mLastLocation.getLongitude();
            MainActivity.mUser.add("CurrentLocation", lat);
            MainActivity.mUser.add("CurrentLocation", long);
            MainActivity.mUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        classifyintoTent(lat,long );
                    }
                }
            });
            Toast.makeText(getApplicationContext(), "" + mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
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


}
