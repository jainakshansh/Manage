package com.akshanshjain.manage.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.akshanshjain.manage.Database.ExpenseContract.ExpenseEntry;

/**
 * Created by Akshansh on 08-06-2017.
 */

public class ExpenseProvider extends ContentProvider {

    //Database Helper Object.
    private ExpenseDbHelper mDbHelper;

    //URI Matcher code for the content URI for expenses table.
    private static final int EXPENSES = 100;

    //URI Matcher code for the content URI for single expense in expenses table.
    private static final int EXPENSES_ID = 101;

    //Creating a URI Matcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This is run the first time anything is called from this class.
    static {

        //This is for content URI of the form "content://com.example.akshansh.manage/expenses" which will map to
        //integer code for EXPENSES. This is used to access the whole expenses table from the database.
        sUriMatcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_EXPENSES, EXPENSES);

        //This is for content URI of the form "content://com.example.akshansh.manage/expenses/16" which will map to
        //integer code for EXPENSES_ID. This is used to access the single row of the expenses table from the database
        //which is present at the given id passed as the parameter.
        sUriMatcher.addURI(ExpenseContract.CONTENT_AUTHORITY, ExpenseContract.PATH_EXPENSES + "/#", EXPENSES_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ExpenseDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Getting readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        //Figuring out if URI matcher can match URI to specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                //Querying the complete Expenses table with given arguments. The cursor could contain multiple rows of the expense table.
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
        Setting notification URI on the cursor,
        so we know what content URI the cursor was created for.
        If the data at this URI changes, then we need to update the cursor.
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //Returning the cursor.
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                return insertExpense(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }
    }

    /*
    Insert expense into the database with given content values. Return the new
    content URI for that specific row in the database.
     */
    private Uri insertExpense(Uri uri, ContentValues values) {

        //Checking that title is not null.
        String title = values.getAsString(ExpenseEntry.EXPENSE_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Expense requires a title.");
        }

        //Checking that amount is not null and greater than or equal to zero.
        Float amount = values.getAsFloat(ExpenseEntry.EXPENSE_AMOUNT);
        if (amount != null && amount < 0) {
            throw new IllegalArgumentException("Expense requires a valid amount.");
        }

        //Getting writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Inserting the expense with given values.
        long id = database.insert(ExpenseEntry.TABLE_NAME, null, values);

        //If ID is -1, then insertion failed, return null.
        if (id == -1) {
            return null;
        }

        //Notifying all listeners that the data has changed for the expense content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        //Returning the new URI with the ID of newly inserted row appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                return updateExpense(uri, values, selection, selectionArgs);
            case EXPENSES_ID:
                //Extracting ID from the URI so we know which row to update.
                selection = ExpenseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateExpense(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for:" + uri);
        }
    }

    /*
    Updating expenses in the database with the given Content Values.
    Applying changes to the rows for the given selection and selection arguments
    which can be 0 or 1 or more expenses.
    Also returning the number of rows that were successfully updated.
     */
    private int updateExpense(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        //Checking if the key with title is present.
        //If present checking that it's not null.
        if (contentValues.containsKey(ExpenseEntry.EXPENSE_TITLE)) {
            String title = contentValues.getAsString(ExpenseEntry.EXPENSE_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Expense requires a title!");
            }
        }

        //Checking if amount value present and updating if it is.
        if (contentValues.containsKey(ExpenseEntry.EXPENSE_AMOUNT)) {
            Float amount = contentValues.getAsFloat(ExpenseEntry.EXPENSE_AMOUNT);
            if (amount == null || amount < 0) {
                throw new IllegalArgumentException("Expense requires a valid amount.");
            }
        }

        //If there are no values to update then don't change the database.
        if (contentValues.size() == 0) {
            return 0;
        }

        //Otherwise get writable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Performing the update on database and getting the number of rows affected.
        int rowsUpdated = database.update(ExpenseEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        //If one or more rows were updated we will notify all the listeners that given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Returning the number of rows updated.
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Getting writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Tracking the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                //Deleting all rows that match the selection and selection arguments.
                rowsDeleted = database.delete(ExpenseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EXPENSES_ID:
                selection = ExpenseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ExpenseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not supported for: " + uri);
        }

        //If one or more rows deleted then we will notify all the listeners that the URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Returning the number of rows deleted.
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                return ExpenseEntry.CONTENT_LIST_TYPE;
            case EXPENSES_ID:
                return ExpenseContract.ExpenseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
