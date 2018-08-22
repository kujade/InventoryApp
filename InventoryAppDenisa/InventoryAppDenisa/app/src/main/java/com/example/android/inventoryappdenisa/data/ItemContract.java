package com.example.android.inventoryappdenisa.data;
import android.net.Uri;
import android.provider.BaseColumns;
import android.content.ContentResolver;

//contract for the inventory app
public final class ItemContract {
    //name of content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryappdenisa";
    //creating the base of all URI´s
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //possible path
    public static final String PATH_INVENTORY = "storage";

    private ItemContract () {}

    public static final class ItemEntry implements BaseColumns {
        //accessing the data about items in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
       //MIME type for list of items
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        //MIME type for single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        //name of the table
        public static final String TABLE_NAME ="storage";
        //ID for single item
        public static final String COLUMN_ID = BaseColumns._ID;
        //supplier name
        public static final String COLUMN_SUPP_NAME ="supp_name";
        //product name
        public static final String COLUMN_PROD_NAME ="product_name";
        //price of the item
        public static final String COLUMN_PRICE ="price";
        //quantity of the item
        public static final String COLUMN_QUANTITY = "quantity";
        //supplier´s phone number
        public static final String COLUMN_SUPP_PHONE ="supp_phone";
        //values for supplier name
        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_AMAZON = 1;
        public static final int SUPPLIER_GOGO = 2;
        public static final int SUPPLIER_MOBILE_WORLD = 3;
        public static final int SUPPLIER_BLUE_DAY = 4;
        public static final int SUPPLIER_COMPUTERS_INC = 5;
    }
}
