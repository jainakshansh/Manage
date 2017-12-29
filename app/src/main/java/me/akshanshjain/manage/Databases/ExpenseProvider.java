package me.akshanshjain.manage.Databases;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.akshanshjain.manage.Databases.ExpenseContract.ExpenseEntry;

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
        databaseHelper = new ExpenseDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Getting readable database.
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        //The cursor will hold the result of the query.
        Cursor cursor;

        //Figuring out if URI Matcher can match URI to specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                /*
                Querying the complete ajexpenses table from the database with given arguments.
                The cursor can contain multiple rows of the ajexpenses table.
                 */
                cursor = database.query(ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case EXPENSES_ID:
                selection = ExpenseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /*
        Setting a notification URI on the cursor, so we know what content URI the cursor was created for.
        If the data at this URI changes, then we need to update the cursor.
         */

        //Returning the cursor.
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                return ExpenseEntry.CONTENT_LIST_TYPE;
            case EXPENSES_ID:
                return ExpenseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                return insertExpense(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }
    }

    private Uri insertExpense(Uri uri, ContentValues values) {

        //Checking that the title is not null.
        String title = values.getAsString(ExpenseEntry.EXPENSE_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Expense requires a title.");
        }

        //Checking that the type is not null.
        String type = values.getAsString(ExpenseEntry.EXPENSE_TYPE);
        if (type == null) {
            throw new IllegalArgumentException("Expense requires a type.");
        }

        //Checking that the amount for an expense is not null.
        double amount = values.getAsDouble(ExpenseEntry.EXPENSE_AMOUNT);
        if (amount < 0 && amount == 0) {
            throw new IllegalArgumentException("Expense requires a amount.");
        }

        //Checking that the date time is not null.
        String datetime = values.getAsString(ExpenseEntry.EXPENSE_DATE_TIME);
        if (datetime == null) {
            throw new IllegalArgumentException("Expense requires a date time.");
        }

        //Getting writable database.
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        //Inserting the expense with the taken values.
        long id = database.insert(ExpenseEntry.TABLE_NAME, null, values);

        //If ID is -1 then insertion has failed, return null.
        if (id == -1) {
            return null;
        }

        //Notifying all the listeners that the data has changed for the expense content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        //Returning the new URI with the ID of the newsly inserted row appended at the end.
        return ContentUris.withAppendedId(uri, id);
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
