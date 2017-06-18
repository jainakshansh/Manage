package com.akshanshjain.manage.Database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Akshansh on 08-06-2017.
 */

public final class ExpenseContract {

    //Content authority is name for the entire content provider, similar to domain and its website.
    public static final String CONTENT_AUTHORITY = "com.akshanshjain.manage";

    //Creating the base of all URI's which apps will use to contact
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Path for extracting the data from the table.
    public static final String PATH_EXPENSES = "expenses";

    private ExpenseContract() {
    }

    public static final class ExpenseEntry implements BaseColumns {

        //The content URI to access the expense data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSES);

        /*
        The MIME type of content URI for list of expenses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        /*
        The MIME type of Content URI for single expense.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        //Name of table for database for expenses.
        public final static String TABLE_NAME = "expenses";

        public final static String _ID = BaseColumns._ID; //Unique ID number for expenses. FOr use in the database only.
        public final static String EXPENSE_TITLE = "title"; //Title for the expense.
        public final static String EXPENSE_TYPE = "type"; //Type for the expense.
        public final static String EXPENSE_DATE_TIME = "datetime"; //Date of the expense.
        public final static String EXPENSE_AMOUNT = "amount"; //Amount for the expense.
        public final static String EXPENSE_CATEGORY = "category"; //Category for the expense.
        public final static String EXPENSE_COMMENTS = "comments"; //Possible comments for the reviewing in the future.
        public final static String EXPENSE_IMAGE = "image"; //Possible optional transaction image for future references.
    }
}
