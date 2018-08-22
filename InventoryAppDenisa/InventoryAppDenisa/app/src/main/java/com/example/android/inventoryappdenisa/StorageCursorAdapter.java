package com.example.android.inventoryappdenisa;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventoryappdenisa.data.ItemContract;
import com.example.android.inventoryappdenisa.data.ItemContract.ItemEntry;

//adapter for a list view, data source - items data
//create list item for each item in the data set
public class StorageCursorAdapter extends CursorAdapter {
    public StorageCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    //new blank list item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    //binds current item data with list item layout
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //find views which we want to modify
        TextView productTextView =  view.findViewById(R.id.product);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView phoneTextView = view.findViewById(R.id.supp_phone);
        TextView supplierTextView = view.findViewById(R.id.supp_name);

        //find the columns in which we are interested in
        int productColumnIndex = cursor.getColumnIndex( ItemContract.ItemEntry.COLUMN_PROD_NAME );
        int quantityColumnIndex = cursor.getColumnIndex( ItemContract.ItemEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex( ItemContract.ItemEntry.COLUMN_PRICE);
        int phoneColumnIndex = cursor.getColumnIndex( ItemContract.ItemEntry.COLUMN_SUPP_PHONE);
        int supplierColumnIndex = cursor.getColumnIndex( ItemContract.ItemEntry.COLUMN_SUPP_NAME);

        //read attributes of item from cursor
        String productName = cursor.getString(productColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String phone = cursor.getString(phoneColumnIndex);
        String supplier = cursor.getString(supplierColumnIndex);

        //update text views
        productTextView.setText( productName );
        quantityTextView.setText(quantity);
        priceTextView.setText(price);
        phoneTextView.setText(phone);
        supplierTextView.setText(supplier);

        // column number of "_ID"
        int productIdIndex = cursor.getColumnIndex(ItemEntry._ID);

        //get info from cursor for item because of sale button
        final long prodIdValu = Integer.parseInt(cursor.getString(productIdIndex));
        final int quantInt = cursor.getInt(quantityColumnIndex);

        //sale button for items, decrease quanitity by one
        Button saleButton = view.findViewById(R.id.button_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri currentUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, prodIdValu);
                String updateOfQuant = String.valueOf(quantInt - 1);
                if(Integer.parseInt(updateOfQuant)>=0){
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_QUANTITY,updateOfQuant);
                    context.getContentResolver().update(currentUri,values,null,null);
                }
                //Creating a Toast message that when the quantity is 0 this will be shown
                else {
                    Toast.makeText(context, "Sorry, we are sold out!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}