package com.vivek.snapshary;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class JoinSession extends ActionBarActivity {
    private EditText mSubmitCode;
    private String mSessionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);
        mSubmitCode = (EditText) findViewById(R.id.submitCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_session, menu);
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

    public void join(ParseObject session){
        session.add("friends", MainActivity.mUser);
        session.saveInBackground();
    }


    public void findSession(View v){
        mSessionCode = mSubmitCode.getText().toString();
        ParseQuery<ParseObject> sessionQuery = ParseQuery.getQuery("Session");
        sessionQuery.whereEqualTo("SessionID", mSessionCode);
        Log.i("Session ID", mSessionCode);
        sessionQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sessionList, ParseException e) {
                if (e == null) {
                    if (sessionList.size() == 0)
                        Toast.makeText(getApplicationContext(), "There's no session", Toast.LENGTH_SHORT).show();
                    else
                        Log.d("SESSION LIST", "" +sessionList.size());
//                        Toast.makeText(getApplicationContext(), "There's a session"+sessionList.size(), Toast.LENGTH_SHORT).show();
                        join(sessionList.get(0));

                } else {
                    Log.d("USER", "Error: " + e.getMessage());
                }
            }
        });

    }


}
