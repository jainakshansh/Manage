package com.akshanshjain.manage;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akshanshjain.manage.Database.ExpenseContract.ExpenseEntry;

/**
 * Created by Akshansh on 09-06-2017.
 */

public class ExpenseCursorAdapter extends CursorAdapter {

    public ExpenseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    //Used to inflate a new view and return it. We do not bind any data to it at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
    }

    //Used to bind all the data to given views in a layout file.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Finding the views to populate from the database.
        View typeExpense = view.findViewById(R.id.type_expense_item);
        TextView titleExpense = (TextView) view.findViewById(R.id.title_expense_item);
        TextView dateExpense = (TextView) view.findViewById(R.id.date_expense_item);
        TextView amountExpense = (TextView) view.findViewById(R.id.amount_expense_item);

        //Extracting the attributes of expense from the cursor.
        String type = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_TYPE));
        String title = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_TITLE));
        String date = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_DATE_TIME));
        String amount = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT));

        if (type.equals("Debit")) {
            typeExpense.setBackgroundResource(R.color.middleGreen);
        } else {
            typeExpense.setBackgroundResource(R.color.vermillion);
        }
        titleExpense.setText(title);
        dateExpense.setText(date);
        amountExpense.setText("Rs.\n" + amount);
    }
}
