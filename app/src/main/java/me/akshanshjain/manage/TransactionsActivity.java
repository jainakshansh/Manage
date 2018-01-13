package me.akshanshjain.manage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import me.akshanshjain.manage.Adapters.ExpenseCursorAdapter;

public class TransactionsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;

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
        Defining the Cursor Adapter for the expenses.
        Passing in null until the loader finishes retrieving the data.
         */
        expenseCursorAdapter = new ExpenseCursorAdapter(this, null);

        //List view which will be populated by the data from the database.
        listView = findViewById(R.id.list_view_transactions);
        listView.setAdapter(expenseCursorAdapter);
    }
}
