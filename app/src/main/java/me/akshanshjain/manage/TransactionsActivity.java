package me.akshanshjain.manage;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import me.akshanshjain.manage.Adapters.ExpenseCursorAdapter;
import me.akshanshjain.manage.Databases.ExpenseContract.ExpenseEntry;

public class TransactionsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar toolbar;
    private ListView listView;
    private View emptyView;
    private TextView noTransactions;
    private Button getStarted;

    private Typeface quicksand_bold, quicksand_medium;

    private static final int EXPENSE_LOADER = 9;
    private ExpenseCursorAdapter expenseCursorAdapter;

    /*
    Bottom Sheet Variable List Start.
     */
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;

    private TextView expenseTitle, expenseAmount, expenseDate, expenseCategory;
    private ImageView expenseCategoryIcon, editTransaction;
    private TextView expenseLocation, expenseNotes;
    private Uri currentExpenseURI;
    /*
    Bottom Sheet Variables List End.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        /*
        Setting up the toolbar for the activity.
         */
        toolbar = findViewById(R.id.toolbar_transactions);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Transactions");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*
        Defining the typefaces to be used.
         */
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");
        noTransactions = findViewById(R.id.no_transactions_text);
        noTransactions.setTypeface(quicksand_bold);
        getStarted = findViewById(R.id.get_started_transcations);
        getStarted.setTypeface(quicksand_bold);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewExpenseActivity.class));
                finish();
            }
        });

        /*
        Defining the Cursor Adapter for the expenses.
        Passing in null until the loader finishes retrieving the data.
         */
        expenseCursorAdapter = new ExpenseCursorAdapter(this, null);

        //List view which will be populated by the data from the database.
        listView = findViewById(R.id.list_view_transactions);
        listView.setAdapter(expenseCursorAdapter);

        /*
        Setting an empty view on the list when the transactions haven't yet been loaded from the database.
         */
        emptyView = findViewById(R.id.empty_view_transactions);
        listView.setEmptyView(emptyView);

        //Starting the loader to read the database information.
        getLoaderManager().initLoader(EXPENSE_LOADER, null, this);

        bottomSheetDialog = new BottomSheetDialog(TransactionsActivity.this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bottomSheetView = getLayoutInflater().inflate(R.layout.detailed_bottom_sheet, parent, false);
                bottomSheetDialog.setContentView(bottomSheetView);
                //Forming the content URI that represents the specific expense clicked on.
                currentExpenseURI = ContentUris.withAppendedId(ExpenseEntry.CONTENT_URI, id);
                bottomSheetFunctions();
                bottomSheetDialog.show();
                Log.d("ADebug", expenseNotes.getText().toString());
            }
        });
    }

    /*
    Initialising and referencing all the views from the Bottom Sheet Layout.
    All the views are then used to bind to the data and display data in a beautiful fashion.
    Also includes button for editing the current data which takes the user to editor activity.
     */
    private void bottomSheetFunctions() {
        editTransaction = bottomSheetView.findViewById(R.id.expense_edit_display);
        expenseTitle = bottomSheetView.findViewById(R.id.expense_title_display);
        expenseTitle.setTypeface(quicksand_bold);
        expenseAmount = bottomSheetView.findViewById(R.id.expense_amount_display);
        expenseAmount.setTypeface(quicksand_bold);
        expenseCategory = bottomSheetView.findViewById(R.id.expense_category_display);
        expenseCategory.setTypeface(quicksand_medium);
        expenseCategoryIcon = bottomSheetView.findViewById(R.id.category_icon_display);
        expenseDate = bottomSheetView.findViewById(R.id.expense_date_display);
        expenseDate.setTypeface(quicksand_medium);
        expenseLocation = bottomSheetView.findViewById(R.id.expense_location_display);
        expenseLocation.setTypeface(quicksand_medium);
        expenseNotes = bottomSheetView.findViewById(R.id.expense_notes_display);
        expenseNotes.setTypeface(quicksand_medium);

        Cursor cursor = expenseCursorAdapter.getCursor();
        expenseTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        expenseCategory.setText(cursor.getString(cursor.getColumnIndex("category")));
        expenseDate.setText(cursor.getString(cursor.getColumnIndex("datetime")));
        expenseAmount.setText(cursor.getString(cursor.getColumnIndex("amount")));
        expenseNotes.setText(cursor.getString(cursor.getColumnIndex("notes")));
        expenseLocation.setText(cursor.getString(cursor.getColumnIndex("location")));

        //Setting corresponding icon to the category type.
        switch (cursor.getString(cursor.getColumnIndex("category"))) {
            case "Daily":
                expenseCategoryIcon.setImageResource(R.drawable.ic_daily);
                break;
            case "Education":
                expenseCategoryIcon.setImageResource(R.drawable.ic_education);
                break;
            case "Entertainment":
                expenseCategoryIcon.setImageResource(R.drawable.ic_entertainment);
                break;
            case "Fuel":
                expenseCategoryIcon.setImageResource(R.drawable.ic_fuel);
                break;
            case "Maintenance":
                expenseCategoryIcon.setImageResource(R.drawable.ic_maintenance);
                break;
            case "Meals":
                expenseCategoryIcon.setImageResource(R.drawable.ic_meals);
                break;
            case "Office":
                expenseCategoryIcon.setImageResource(R.drawable.ic_office);
                break;
            case "Personal":
                expenseCategoryIcon.setImageResource(R.drawable.ic_personal);
                break;
            case "Travel":
                expenseCategoryIcon.setImageResource(R.drawable.ic_travel);
                break;
        }

        //Taking user to editor activity.
        editTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editExpenseIntent = new Intent(getApplicationContext(), NewExpenseActivity.class);

                //Setting the URI on the data field of the intent.
                editExpenseIntent.setData(currentExpenseURI);
                startActivity(editExpenseIntent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*
        Defining the projection that specifies the columns from the table.
         */
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.EXPENSE_TITLE,
                ExpenseEntry.EXPENSE_TYPE,
                ExpenseEntry.EXPENSE_AMOUNT,
                ExpenseEntry.EXPENSE_CATEGORY,
                ExpenseEntry.EXPENSE_DATE_TIME,
                ExpenseEntry.EXPENSE_NOTES,
                ExpenseEntry.EXPENSE_LOCATION
        };

        /*
        This loader will execute the Content Provider's query method on a background thread.
         */
        return new CursorLoader(this,
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                ExpenseEntry._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*
        Updating the Expense Cursor Adapter data with the new content to bind data to views.
         */
        expenseCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
        Callback to when the retrieved temporary data need to be lost.
         */
        expenseCursorAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
