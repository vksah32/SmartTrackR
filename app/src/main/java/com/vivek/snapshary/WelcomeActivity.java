package com.vivek.snapshary;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class WelcomeActivity  extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected;
    LocationRequest mLocationRequest;

    private EditText myLat;
    private EditText myLon;
    private EditText oneLat;
    private EditText oneLon;
    private EditText twoLat;
    private EditText twoLon;
    private EditText oneDist;
    private EditText twoDist;
    private EditText status;

    private static final String LOG_TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        myLat = (EditText) findViewById(R.id.myLat);
        myLon = (EditText) findViewById(R.id.myLon);
        oneLat = (EditText) findViewById(R.id.oneLat);
        oneLon = (EditText) findViewById(R.id.oneLon);
        twoLat = (EditText) findViewById(R.id.twoLat);
        twoLon = (EditText) findViewById(R.id.twoLon);
        oneDist = (EditText) findViewById(R.id.oneDist);
        twoDist = (EditText) findViewById(R.id.twoDist);
        status = (EditText) findViewById(R.id.status);

        ParseQuery<ParseObject> tentQuery = ParseQuery.getQuery("Tent");
//        tentQuery.whereEqualTo("Radius", 15); //NOTE: This is just a shortcut for now to get all the rows, have to make it more generalized later
        tentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> tentList, ParseException e) {
                if (e == null) {
                    ParseObject one = tentList.get(0);
                    oneLat.setText(String.valueOf(one.getDouble("Lat")));
                    oneLon.setText(String .valueOf(one.getDouble("Lon")));

                    ParseObject two = tentList.get(1);
                    twoLat.setText(String.valueOf(two.getDouble("Lat")));
                    twoLon.setText(String.valueOf(two.getDouble("Lon")));
                } else {

                }
            }
        });

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

    public void agree(View v) {
        sendRequest();

    }

    public void classifyintoTent(final Double lat, final Double lon) {
        myLat.setText(String.valueOf(lat));
        myLon.setText(String.valueOf(lon));
        ParseQuery<ParseObject> tentQuery = ParseQuery.getQuery("Tent");
//        tentQuery.whereEqualTo("Radius", 15); //NOTE: This is just a shortcut for now to get all the rows, have to make it more generalized later
        tentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> tentList, ParseException e) {
                if (e == null) {
                    checkTent(lat, lon, tentList);
                    Log.d("USERLIST: ", "" + tentList);
                } else {
                    Log.d("USER", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void checkTent(Double lat, Double lon, List<ParseObject> tentList) {
        int c = 0;
        for (final ParseObject tent : tentList) {
            Double tentLat = (double) tent.getNumber("Lat");
            Double tentLon = (double) tent.getNumber("Lon");
            Double tentRadius = tent.getNumber("Radius").doubleValue();
            Double x2 = (lat - tentLat) * (lat - tentLat);
            Double y2 = (lon - tentLon) * (lon - tentLon);
            Double dist = Math.sqrt(x2 + y2);

            if (c==0) { // tent one
                oneDist.setText(String.valueOf(dist));
            } else if (c==1) { // tent two
                twoDist.setText(String.valueOf(dist));
            }

            if (dist < tentRadius) {
                status.setText("In Tent " + (c+1));
                ParseQuery<ParseObject> query = ParseQuery.getQuery(MainActivity.USER_TABLE_NAME);

                query.getInBackground(MainActivity.getUserID(this), new GetCallback<ParseObject>() {
                    public void done(ParseObject user, ParseException e) {
                        if (e == null) {
                            // Now let's update it with some new data. In this case, only cheatMode and score
                            // will get sent to the Parse Cloud. playerName hasn't changed.
                            user.put("CurrentTent", tent.getObjectId());
                            user.saveInBackground();
                        }
                    }
                });
                //return; // exit after finding the first tent
            }
            c++;
        }
//        status.setText("Moving");
    }

    public void sendRequest() {
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
                    Double lat = mLastLocation.getLatitude();
                    Double lon = mLastLocation.getLongitude();
                    if (e == null) {
                        classifyintoTent(lat, lon);
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
//        Toast.makeText(this,"STARTED LOC UPDATE", Toast.LENGTH_SHORT).show();
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


//        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
        createLocationRequest();
        if (mLocationRequest != null)
            startLocationUpdates();


    }


}
