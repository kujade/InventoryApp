package com.example.android.inventoryappdenisa;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.android.inventoryappdenisa.data.ItemContract.ItemEntry;
import com.example.android.inventoryappdenisa.data.StorageDbHelper;

//create or edit an item
public class EditorActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private final int MIN_QUANT= 0;
    private final int MAX_QUANT = 99;

    //identify item data loader
    private static final int EXISTING_ITEM_LOADER = 0;

    //content URI for existing item
    private Uri mCurrentItemUri;
    private Spinner mSupplierSpinner;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mPhoneEditText;
    private Button minusButton;
    private Button plusButton;
    private Button phoneButton;

    public StorageDbHelper dbHelper;
    //value of supplier; 1 for amazon, 2 for GoGo, 3 for Mobile World, 4 for Blue Day, 5 for Computers INC
    private int mSupplier = ItemEntry.SUPPLIER_UNKNOWN;

    //identify is item was edited
    private boolean mItemHasChanged = false;

    //listen to clicks on view and if the view should be edited
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

    Intent intent = getIntent();
    mCurrentItemUri = intent.getData();

    if (mCurrentItemUri == null) {
        // new item, app bar says "new item"
        setTitle(getString(R.string.editor_activity_title_new_item));
        //hide deleting option
        invalidateOptionsMenu();
    } else {
        //existing item, app bar says "edit item"
        setTitle(getString(R.string.editor_activity_title_edit_item));
        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
    }
        // views for user input
        mSupplierSpinner =  findViewById(R.id.spinner_supplier);
        mNameEditText =  findViewById(R.id.edit_product_name);
        mPriceEditText =  findViewById(R.id.edit_price);
        mQuantityEditText =  findViewById(R.id.edit_quantity);
        mPhoneEditText =  findViewById( R.id.edit_phone );
        plusButton = findViewById(R.id.plus_button);
        minusButton = findViewById(R.id.minus_button);
        phoneButton = findViewById(R.id.phone_button);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = mQuantityEditText.getText().toString();
                int quantityInt;
                if(quantity.length() == 0){
                    quantityInt = 0;
                    mQuantityEditText.setText(String.valueOf(quantityInt));
                }else{
                    quantityInt = Integer.parseInt(quantity) - 1;
                    if(quantityInt >=MIN_QUANT) {
                        mQuantityEditText.setText(String.valueOf(quantityInt));
                    }
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = mQuantityEditText.getText().toString();
                int quantityInt;
                if(quantity.length() == 0){
                    quantityInt = 1;
                    mQuantityEditText.setText(String.valueOf(quantityInt));
                }else{
                    quantityInt = Integer.parseInt(quantity) + 1;
                    if(quantityInt<=MAX_QUANT) {
                        mQuantityEditText.setText(String.valueOf(quantityInt));
                    }
                }
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel: " + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

        });

        dbHelper = new StorageDbHelper(this);

        mNameEditText.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    //spinner for selecting the supplier (from array of suppliers)
    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSupplierSpinner.setAdapter(supplierSpinnerAdapter);
        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_amazon))) {
                        mSupplier = ItemEntry.SUPPLIER_AMAZON;
                    } else if (selection.equals(getString(R.string.supplier_gogo))) {
                        mSupplier = ItemEntry.SUPPLIER_GOGO;
                    } else if (selection.equals(getString(R.string.supplier_mobile_world))) {
                        mSupplier = ItemEntry.SUPPLIER_MOBILE_WORLD;
                    } else if (selection.equals(getString(R.string.supplier_blue_day))) {
                        mSupplier = ItemEntry.SUPPLIER_BLUE_DAY;
                    } else if (selection.equals(getString(R.string.supplier_computers_inc))) {
                        mSupplier = ItemEntry.SUPPLIER_COMPUTERS_INC;
                    } else {
                        mSupplier = ItemEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }
            // onNothingSelected have to be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = ItemEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    //back button click method
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //extract input from user from editor and save item
    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        //control if it is going to be a new item
        //control blank fields

            if (mSupplier == ItemEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.need_supp), Toast.LENGTH_SHORT).show();
                return;
            }
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.need_name), Toast.LENGTH_SHORT).show();
            return;
        }
            if (TextUtils.isEmpty(priceString)) {
                 Toast.makeText(this, getString(R.string.need_price), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, getString(R.string.need_quant), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(phoneString)) {
                Toast.makeText(this, getString(R.string.need_phone), Toast.LENGTH_SHORT).show();
                return;
            }
        //creation of Contentvalues
        //colummn names = keys
        //item attributies = values
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_PROD_NAME, nameString);
        values.put(ItemEntry.COLUMN_PRICE, priceString);
        values.put(ItemEntry.COLUMN_QUANTITY, quantityString);
        values.put(ItemEntry.COLUMN_SUPP_PHONE, phoneString);
        values.put(ItemEntry.COLUMN_SUPP_NAME, mSupplier);
        if (mCurrentItemUri == null)  {
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
            // toast message for succesful or failed insert
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_succes),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            //existing item, update content Uri
            int rowsAffected = getContentResolver().update
                    (mCurrentItemUri, values, null, null);
            // toast message for succesful or failed insert
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_succes),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
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
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //updating menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // save action - respond to it
                saveItem();
                return true;
            case R.id.action_delete:
                // delete action -repond to it
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                //item didn´t change, go to parent activity
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PROD_NAME,
                ItemEntry.COLUMN_SUPP_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_SUPP_PHONE};
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PROD_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPP_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY);
            int phoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPP_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int supplier = cursor.getInt(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mPhoneEditText.setText(Integer.toString(phone));

            switch (supplier) {
                case ItemEntry.SUPPLIER_AMAZON:
                    mSupplierSpinner.setSelection(1);
                    break;
                case ItemEntry.SUPPLIER_GOGO:
                    mSupplierSpinner.setSelection(2);
                    break;
                case ItemEntry.SUPPLIER_MOBILE_WORLD:
                    mSupplierSpinner.setSelection(3);
                    break;
                case ItemEntry.SUPPLIER_BLUE_DAY:
                    mSupplierSpinner.setSelection(4);
                    break;
                case ItemEntry.SUPPLIER_COMPUTERS_INC:
                    mSupplierSpinner.setSelection(5);
                    break;
                default:
                    mSupplierSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mPhoneEditText.setText("");
        mSupplierSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}







