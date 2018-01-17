package me.akshanshjain.manage.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import me.akshanshjain.manage.Databases.ExpenseContract.ExpenseEntry;
import me.akshanshjain.manage.R;

/**
 * Created by Akshansh on 13-01-2018.
 */

public class ExpenseCursorAdapter extends CursorAdapter {

    private Typeface quicksand_bold, quicksand_medium;

    /*
    Constructor for the Adapter taking in context and the cursor.
     */
    public ExpenseCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        quicksand_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand_Medium.ttf");
    }

    /*
    Inflating a new view and returning it. We do not bind any data at this point.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.single_expense_item, parent, false);
    }

    /*
    Getting all the data from the database and binding it to the layout.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Initialising and referencing all the views from the XML.
        TextView expenseTitle = view.findViewById(R.id.expense_title);
        expenseTitle.setTypeface(quicksand_bold);
        TextView expenseAmount = view.findViewById(R.id.expense_amount);
        expenseAmount.setTypeface(quicksand_bold);
        TextView expenseCategory = view.findViewById(R.id.expense_category);
        expenseCategory.setTypeface(quicksand_medium);
        TextView expenseDate = view.findViewById(R.id.expense_date);
        expenseDate.setTypeface(quicksand_medium);

        //Extracting all the data from the database.
        String title = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_TITLE));
        String amount = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT));
        String category = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_CATEGORY));
        String date = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_DATE_TIME));
        String type = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_TYPE));
        String location = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_LOCATION));
        switch (type) {
            case "Income":
                expenseAmount.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.palmLeaf));
                break;
            case "Expense":
                expenseAmount.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.bostonUniRed));
                break;
        }

        //Setting the extracted data into the views.
        expenseTitle.setText(title);
        expenseAmount.setText("₹ " + amount);
        expenseCategory.setText(category);
        expenseDate.setText(date);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}