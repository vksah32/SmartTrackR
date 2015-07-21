package com.vivek.snapshary;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


public class inviteActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Contact data
        String columnsToExtract[] = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        ContentResolver contentresolver = getContentResolver();


        String whereClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "<>''" + " AND "
                + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1" + " AND "
                + ContactsContract.Contacts.HAS_PHONE_NUMBER;



        String sortOrder = ContactsContract.Contacts._ID + " ASC";


        //query contacts content provider

        Cursor cursor = contentresolver.query(ContactsContract.Contacts.CONTENT_URI, columnsToExtract, whereClause, null, sortOrder);

        setListAdapter(new ContactInfoListAdapter(this, R.layout.list_item, cursor, 0));
        setContentView(R.layout.activity_invite);
        setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite, menu);
        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("Phone"), Toast.LENGTH_SHORT).show();
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
}
