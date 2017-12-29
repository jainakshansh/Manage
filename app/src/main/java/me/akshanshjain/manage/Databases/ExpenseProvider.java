package me.akshanshjain.manage.Databases;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Akshansh on 29-12-2017.
 */

public class ExpenseProvider extends ContentProvider {

    //Database Helper object.
    private ExpenseDatabaseHelper databaseHelper;

    //URI Matcher code for content URI for the list of expenses from the table.
    private static final int EXPENSES = 9;

    //URI Matcher code for the content URI for a single expense in the table.
    private static final int EXPENSES_ID = 99;

    //Creating a URI Matcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This is run first whenever everything is called from this class.
    static {

        /*
        This is for the content URI of the form "content://me.akshanshjain.manage/ajexpenses"
        which will map to integer code for EXPENSES.
        This is used to access the whole list from the table in the database.
         */
        sUriMatcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_EXPENSES, EXPENSES);

        /*
        This is for the content URI of the form "content://me.akshanshjain.manage/ajexpenses/number"
        which will map to the integer code for EXPENSES_ID.
        This is used to access a single row from the table in the database which is present at the
        given number passed in the parameter.
         */
        sUriMatcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_EXPENSES + "/#", EXPENSES_ID);
    }
    
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
