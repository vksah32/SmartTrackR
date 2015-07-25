package com.vivek.snapshary;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends ActionBarActivity {


    private EditText mPhone;
    static public  ParseObject mSession;
    private String mSessionID;
    private String mAndroidID;
    private EditText mUserName;
    public static ParseObject mUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doesUserExists();

        //only if google account is associated
        setContentView(R.layout.activity_main);
        mUserName = (EditText) findViewById(R.id.name);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void submit(View v){
        String name = mUserName.getText().toString();
        mUser = new ParseObject("Newuser");
        mUser.put("NAME", name);
        mUser.put("AndroidID", getAndroidID());
        mUser.saveInBackground();
        startActivity(new Intent(this, WelcomeActivity.class));

    }

    public void createSession(View v){
//
////       mSession = new Session(mPhone.getText().toString());
//        mSession = new ParseObject("Session");
//        mSession.put("creator", mUser);
//        mSession.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                mSessionID = mSession.getObjectId();
//                Log.i("SESSIONID", mSessionID);
//                Intent i = new Intent(MainActivity.this, SessionCreated.class);
//                i.putExtra("Code",mSessionID);
//                startActivity(i);
//
//            }
//        });



    }

    public void joinSession(View v){
        startActivity(new Intent(MainActivity.this, JoinSession.class));

    }

    public String getAndroidID(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void doesUserExists(){
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("User");
        userQuery.whereEqualTo("androidID", getAndroidID());
        Log.i("ANDROID ID", getAndroidID());
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    if (userList.size() == 0)
                        setUser();
                    else
                        mUser = userList.get(0);

                } else {
                    Log.d("USER", "Error: " + e.getMessage());
                }
            }
        });

    }


    public void setUser(){
        mUser = new ParseObject("User");
        mUser.put("androidID", getAndroidID());
        mUser.saveInBackground();


    }
}
