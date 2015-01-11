package com.example.fadzlirazali.grocerylistapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*Added 2methods  */

    public void onClickAddGrocery(View view){
        //Add a new item
        ContentValues values = new ContentValues();

        values.put(GroceriesProvider.NAME,
                ((EditText)findViewById(R.id.txtName)).getText().toString());

        values.put(GroceriesProvider.GROCERY,
                ((EditText)findViewById(R.id.txtGrocery)).getText().toString());

        /*content provider URI address which will be used to access the content.*/
        Uri uri = getContentResolver().insert(
                GroceriesProvider.CONTENT_URI, values);

        Toast.makeText(getBaseContext(),uri.toString(),Toast.LENGTH_LONG).show();
    }

    public void onClickRetrieveGrocery(View view){
        //Retrive grocery records
        String URL ="content://com.example.provider.Kitchen/groceries";

        /*analyze string from url path*/
        Uri groceries = Uri.parse(URL);

        Cursor c = managedQuery(groceries, null,null,null, "name");
        if(c.moveToFirst()){
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(GroceriesProvider._ID)) +
                                ", " + c.getString(c.getColumnIndex(GroceriesProvider.NAME))+
                                ", " + c.getString(c.getColumnIndex(GroceriesProvider.GROCERY)),
                        Toast.LENGTH_SHORT).show();
            }while(c.moveToNext());
        }

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
