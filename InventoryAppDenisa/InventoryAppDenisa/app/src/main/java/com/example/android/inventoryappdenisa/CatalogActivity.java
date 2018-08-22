package com.example.android.inventoryappdenisa;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.inventoryappdenisa.data.ItemContract.ItemEntry;

//populate list of items entered and saved in database
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ITEM_LOADER = 0;
    StorageCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // floating action button opens editing activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // find the list view
        ListView itemListView =  findViewById(R.id.list);

        // find empty view and set if there are no items in the database
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        //setup adapter for creating list item, no data yet so got null
        mCursorAdapter = new StorageCursorAdapter (this, null);
        itemListView.setAdapter( mCursorAdapter );

        //item listener set up
        itemListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {
            Intent intent = new Intent (CatalogActivity.this, EditorActivity.class);
            Uri currentItemUri = ContentUris.withAppendedId( ItemEntry.CONTENT_URI, id );
            intent.setData(currentItemUri);
            startActivity(intent);
            }
        } );

        //start loader
        getLoaderManager().initLoader( ITEM_LOADER, null, this );
    }

    //helping method to insert dummy data to discover errors
    private void insertItem () {
        ContentValues values = new ContentValues(  );
        values.put(ItemEntry.COLUMN_SUPP_NAME, ItemEntry.SUPPLIER_AMAZON);
        values.put( ItemEntry.COLUMN_PROD_NAME,"Phone" );
        values.put( ItemEntry.COLUMN_PRICE, 1000 );
        values.put( ItemEntry.COLUMN_QUANTITY, 6 );
        values.put( ItemEntry.COLUMN_SUPP_PHONE, 987098456 );
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values );
    }

    //deleting all items in database, helping method
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from storage database");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllItems();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    //create menu in the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummy:
                // insert dummy data
                insertItem();
                return true;
            case R.id.delete_all:
                //delete all items in database
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PROD_NAME,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_SUPP_NAME,
                ItemEntry.COLUMN_SUPP_PHONE,
        };
        return new CursorLoader(  this, ItemEntry.CONTENT_URI, projection, null,
                null, null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}
