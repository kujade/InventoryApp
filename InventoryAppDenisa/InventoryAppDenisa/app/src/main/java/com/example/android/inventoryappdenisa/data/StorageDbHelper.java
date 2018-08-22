package com.example.android.inventoryappdenisa.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryappdenisa.data.ItemContract.ItemEntry;

//database helper, manages versions and creations
public class StorageDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = StorageDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    public StorageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //executed at first creation of database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STORAGE_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + "("
                + ItemEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemEntry.COLUMN_SUPP_NAME + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_PROD_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_QUANTITY  + " INTEGER NOT NULL DEFAULT 0,"
                + ItemEntry.COLUMN_SUPP_PHONE + " INTEGER);";
        db.execSQL(SQL_CREATE_STORAGE_TABLE);
    }

    //upgrading od the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
