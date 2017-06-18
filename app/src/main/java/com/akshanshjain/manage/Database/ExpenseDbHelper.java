package com.akshanshjain.manage.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akshanshjain.manage.Database.ExpenseContract.ExpenseEntry;
/**
 * Created by Akshansh on 08-06-2017.
 */

public class ExpenseDbHelper extends SQLiteOpenHelper {

    //Name of the database file
    private static final String DATABASE_NAME = "expenses.db";

    //Database Version. Needs to be incremented if schema changed.
    private static final int DATABASE_VERSION = 1;

    //Creating a constructor
    public ExpenseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating Table for Expenses

        String SQL_CREATE_EXPENSES_TABLE = "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " ("
                + ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExpenseEntry.EXPENSE_TYPE + " TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_TITLE + " TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_DATE_TIME + " TEXT NOT NULL, "
                + ExpenseEntry.EXPENSE_AMOUNT + " INTEGER NOT NULL DEFAULT 0, "
                + ExpenseEntry.EXPENSE_CATEGORY + " TEXT, "
                + ExpenseEntry.EXPENSE_COMMENTS + " TEXT, "
                + ExpenseEntry.EXPENSE_IMAGE + " BLOB );";

        db.execSQL(SQL_CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        Currently our database is at version 1 so there is nothing to be done here.
         */
    }
}