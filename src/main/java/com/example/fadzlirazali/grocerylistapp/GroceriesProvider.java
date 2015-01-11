package com.example.fadzlirazali.grocerylistapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by FadzliRazali on 1/12/15 based on Tutorialpoint.com
 */
public class GroceriesProvider extends ContentProvider {


    /*PROPERTIES DECLARATIONS*/
    static final String PROVIDER_NAME= "com.example.provider.Kitchen";
    static final String URL="content://" + PROVIDER_NAME + "/groceries";
    static final Uri CONTENT_URI = Uri.parse(URL);

    /*property item*/
    static final String _ID="_id";
    static final String NAME="name";
    static final String GROCERY="grocery";

    private static HashMap<String, String> GROCERIES_PROJECTION_MAP;

    static final int GROCERIES=1;
    static final int GROCERY_ID=2;

    /*URI untuk dptkan grocery by item id */
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher (UriMatcher.NO_MATCH);

        /*tambah uri tuk 1 grocery sahaja*/
        uriMatcher.addURI(PROVIDER_NAME, "groceries", GROCERIES);

        uriMatcher.addURI(PROVIDER_NAME,"groceries/#",GROCERY_ID);
    }

    /*
    * Database specific constant declarations
    *
    * */
    private SQLiteDatabase db;
    static final String DATABASE_NAME="Kitchen";
    static final String GROCERIES_TABLE_NAME="groceries";
    static final int DATABASE_VERSION=1;
    static final String CREATE_DB_TABLE=
            "CREATE TABLE " + GROCERIES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    " name TEXT NOT NULL,"+
                    " grocery TEXT NOT NULL);";






    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            /*sql the SQL statement to be executed*/
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        /*database needs to be upgraded if has an old one*/
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+ GROCERIES_TABLE_NAME);
            onCreate(db);
        }

    }

    /*First activity load*/
    @Override
    public boolean onCreate() {
        /*Context allows access to application-specific resources and classes*/
        Context context = getContext();

        /* creates and manages the provider's underlying data repository*/
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /*
        * Create a write able db which will trigger its creation if doesnt exits
        * */
        db=dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
        * Add a new item record
        *
        * insert(table to insert the row into, optional,contains the initial column values)
        * */
        long rowID = db.insert(GROCERIES_TABLE_NAME,"",values);

        try {
            /*
            * if record is added
            * */
            if(rowID>0){
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }

            throw new SQLException("Failed to add a record into "+ uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(GROCERIES_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case GROCERIES:
                qb.setProjectionMap(GROCERIES_PROJECTION_MAP);
                break;

            case GROCERY_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI"+ uri);
        }

        if(sortOrder==null || sortOrder==""){
            /*
            * By default sort on grocery name
            * */
            sortOrder=NAME;
        }
        Cursor c =qb.query(db,projection, selection,selectionArgs, null,null,sortOrder);

        /*
        * register to watch a content URI for changes
        * */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count =0;

        switch (uriMatcher.match(uri)){
            case GROCERIES:
                count = db.delete(GROCERIES_TABLE_NAME, selection,selectionArgs);
                break;

            case GROCERY_ID:
                String id = uri.getPathSegments().get(1);
                count=db.delete(GROCERIES_TABLE_NAME, _ID + " = "+ id + (!TextUtils.isEmpty(selection)?" AND ("+
                selection+ ')': ""),selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " +uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count =0;

        switch (uriMatcher.match(uri)){
            case GROCERIES:
                count=db.update(GROCERIES_TABLE_NAME,values,_ID + " = " + uri.getPathSegments().get(1)+
                        (!TextUtils.isEmpty(selection)?" AND (" +
                        selection+ ')':""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unkown URI "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /*
            * Get all groceries record
            *
            * */
            case GROCERIES:
                return "vnd.android.cursor.dir/vnd.example.groceries";

            /*
            * Get a particular grocery
            * */
            case GROCERY_ID:
                return "vnd.android.cursor.item/vnd.example.groceries";

            default:
                throw new IllegalArgumentException("Unsupported URI: "+uri);
          }


    }
}
