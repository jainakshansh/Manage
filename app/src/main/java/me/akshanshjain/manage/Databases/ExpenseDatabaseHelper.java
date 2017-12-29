package me.akshanshjain.manage.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.akshanshjain.manage.Databases.ExpenseContract.ExpenseEntry;

/**
 * Created by Akshansh on 29-12-2017.
 */

public class ExpenseDatabaseHelper extends SQLiteOpenHelper {

    //Name of the database file.
    private static final String DATABASE_NAME = "ajexpenses.db";

    //Database version. Needs to be changed (incremented in case of number) when schema changed.
    private static final int DATABASE_VERSION = 1;

    //Creating a Constructor.
    public ExpenseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating table for expenses.
        String SQL_CREATE_TABLE_EXPENSES = "CREATE TABLE " + ExpenseEntry.TABLE_NAME
                + ExpenseEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExpenseEntry.EXPENSE_TITLE + "TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_TYPE + "TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_AMOUNT + "TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_DATE_TIME + "TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_CATEGORY + "TEXT, "
                + ExpenseEntry.EXPENSE_LOCATION + "TEXT, "
                + ExpenseEntry.EXPENSE_NOTES + "TEXT, "
                + ExpenseEntry.EXPENSE_IMAGE + "BLOB );";

        db.execSQL(SQL_CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        Currently the database is at version 1. So we don't have to do anything here.
         */
    }
}
