package com.example.android.inventoryappdenisa.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.inventoryappdenisa.data.ItemContract.ItemEntry;

public class ItemProvider extends ContentProvider {
    //log messages tag
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();
    //matcher code for URI storage table
    private static final int STORAGE = 100;
    //matcher code for URI storage table specific row
    private static final int ITEM_ID = 101;

    //URI matcher for matching content and its code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_INVENTORY, STORAGE);

        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_INVENTORY + "/#", ITEM_ID);
    }

    //helper object for database
    private StorageDbHelper mDbHelper;
    //creating provider and database helper object
    @Override
    public boolean onCreate() {
        mDbHelper = new StorageDbHelper( getContext() );
        return true;
    }

   //query of given URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match( uri );
        switch (match){
            case STORAGE :
                cursor = database.query( ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                null,sortOrder);
                break;
            case ITEM_ID :
                selection = ItemEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf( ContentUris.parseId( uri ) )};
                cursor = database.query( ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null,null, sortOrder);
                break;
            default :
                throw new IllegalArgumentException( "Unknown URI cannot query " + uri );
        }

       //set notification URI on cursor, if data in URI change we are notify that we need
        //to update cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

   //inserting new data in provider
   @Override
   public Uri insert(Uri uri, ContentValues contentValues) {
       final int match = sUriMatcher.match(uri);
       switch (match) {
           case STORAGE:
               return insertItem(uri, contentValues);
           default:
               throw new IllegalArgumentException("Insert failed because " + uri);
       }
   }

    //insert new item into the database
    private Uri insertItem(Uri uri, ContentValues values) {
        // checking if product name is not null
        String name = values.getAsString(ItemEntry.COLUMN_PROD_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Require a name of the item ");
        }
        // checking if price is < or = 0
        Integer price = values.getAsInteger(ItemEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Valid price required ");
        }
        // checking if quantity is < or = 0
        Integer quantity = values.getAsInteger(ItemEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Valid quantity required ");
        }
        //dont want to check supplier phone and name

        //get database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //insert a new item
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Unable to insert a row because " + uri);
            return null;
        }

        // notify changes of URI for storage content
        getContext().getContentResolver().notifyChange(uri, null);

        //return new URI with id at the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    //updating data
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORAGE:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not support because " + uri);
        }
    }

    //updates items in the database
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // checking if product name is not null
        if (values.containsKey(ItemEntry.COLUMN_PROD_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_PROD_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Requires a name of item ");
            }
        }

        // checking if price is < or = 0
        if (values.containsKey(ItemEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ItemEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Valid price required ");
            }
        }

        // checking if quantity is < or = 0
        if (values.containsKey(ItemEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(ItemEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Valid quantity is required ");
            }
        }

        //no updating of database if there are no changes
        if (values.size() == 0) {
        return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // make and update of database
        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
       //notify listeners that data has changed id there was 1 or more row updated
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // get the number of rows which were updated
        return rowsUpdated;
    }

    //deleting data
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // look after the number of deleted rows
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORAGE:
                // delete all rows
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                // delete a single row according to ID
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported because " + uri);
        }
        // notify all listeners that 1 or more rows has been deleted
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    //returning type of data for URI
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORAGE:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Dont know URI " + uri + " with match " + match);
        }
    }
}

