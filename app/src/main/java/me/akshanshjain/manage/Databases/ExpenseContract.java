package me.akshanshjain.manage.Databases;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Akshansh on 28-12-2017.
 */

public final class ExpenseContract {

    //Content authority is name for the entire content provider, similar to domain and its website.
    public static final String CONTENT_AUTHORITY = "me.akshanshjain.manage";

    //Creating the base of all URI's which app will use to contact.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Path for extracting the data from the table.
    public static final String PATH_EXPENSES = "expenses";

    //Empty constructor initialisation.
    private ExpenseContract() {
    }

    public static final class ExpenseEntry implements BaseColumns {

        //The content URI to access the expense data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSES);

        /*
        The MIME type of content URI for single expense.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        /*
        The MIME type of content URI for list of expenses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        //The name of the table for the database of the Expenses.
        public final static String TABLE_NAME = "expenseTable";

        //The name of the columns in the table.
        //Unique ID number for expenses. For use in the database only.
        public final static String _ID = BaseColumns._ID;
        //Title for the expense. Required.
        public final static String EXPENSE_TITLE = "title";
        //Type of the expense i.e. Expenditure or Income. Required.
        public final static String EXPENSE_TYPE = "type";
        //Amount for the expense. Required.
        public final static String EXPENSE_AMOUNT = "amount";
        //Category of the expense. Required.
        public final static String EXPENSE_CATEGORY = "category";
        //Date and Time of the expense. Required.
        public final static String EXPENSE_DATE_TIME = "datetime";
        //Any additional notes for the expense. Not absolute necessary for entry.
        public final static String EXPENSE_NOTES = "notes";
        //Location for the expense if possible. Not absolute necessary for entry.
        public final static String EXPENSE_LOCATION = "location";
        //Image for any type of expense incurred. Not absolute necessary for entry.
        public final static String EXPENSE_IMAGE = "image";
    }
}
