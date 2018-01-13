package me.akshanshjain.manage;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

    private Typeface quicksand_bold;

    private static final int EXPENSE_LOADER = 9;
    private ExpenseCursorAdapter expenseCursorAdapter;

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
                ExpenseEntry.EXPENSE_DATE_TIME
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
