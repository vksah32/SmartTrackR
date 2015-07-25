package com.vivek.snapshary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    static public ParseObject mUser;
    static public  ParseObject mSession;
    private String mSessionID;
    private String mUserID;
    private String mAndroidID;
    private EditText mUserName;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;

    public static final String PREF_NAME = "pref";
    public static final String PREF_KEY_USER_ID = "userID";
    public static final String USER_TABLE_NAME = "Newuser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doesUserExists();

        TextView loading = new TextView(this);
        loading.setText("Loading...");
        loading.setPadding(100, 100, 40, 40);
        setContentView(loading);
    }

    private void displaySignUpForm() {
        //only if google account is associated
        setContentView(R.layout.activity_main);
        mFirstName = (EditText) findViewById(R.id.firstName);
        mLastName = (EditText) findViewById(R.id.lastName);
        mEmail = (EditText) findViewById(R.id.email);
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
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmail.getText().toString();

        mUser = new ParseObject(USER_TABLE_NAME);
        mUser.put("firstName", firstName);
        mUser.put("lastName", lastName);
        mUser.put("email", email);
        mUser.put("androidID", getAndroidID());
        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i("USERID", mUser.getObjectId());
                redirectUser(mUser);
            }
        });
    }

    public void createSession(View v){

//       mSession = new Session(mPhone.getText().toString());
        mSession = new ParseObject("Session");
        mSession.put("creator", mUser);
        mSession.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mSessionID = mSession.getObjectId();
                Log.i("SESSIONID", mSessionID);
                Intent i = new Intent(MainActivity.this, SessionCreated.class);
                i.putExtra("Code", mSessionID);
                startActivity(i);

            }
        });



    }

    public void joinSession(View v){
        startActivity(new Intent(MainActivity.this, JoinSession.class));

    }

    public String getAndroidID(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void doesUserExists(){
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(USER_TABLE_NAME);
        userQuery.whereEqualTo("androidID", getAndroidID());
        Log.i("ANDROID ID", getAndroidID());
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    if (userList.size() == 0) {
//                        setUser();
                        MainActivity.this.displaySignUpForm();
                    } else {
                        mUser = userList.get(0);
                        redirectUser(mUser);
                    }
                } else {
                    Log.d("USER", "Error: " + e.getMessage());
                }
            }
        });
    }


    public void setUser(){
        mUser = new ParseObject(USER_TABLE_NAME);
        mUser.put("androidID", getAndroidID());
        mUser.saveInBackground();
    }

    public void redirectUser(ParseObject mUser) {
        mUserID = mUser.getObjectId();
        SharedPreferences mPref = MainActivity.this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor mPrefEditor = mPref.edit();
        mPrefEditor.putString(PREF_KEY_USER_ID, mUserID);
        mPrefEditor.commit();

        Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(i);
    }

    public static String getUserID(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        String userID = mPref.getString(MainActivity.PREF_KEY_USER_ID, "");
        return userID;
    }
}
