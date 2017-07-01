package com.akshanshjain.manage;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.akshanshjain.manage.Database.ExpenseContract;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private FloatingSearchView fsv;
    private ExpenseCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fsv = (FloatingSearchView) findViewById(R.id.floating_search_view);
        listView = (ListView) findViewById(R.id.list_view_search);

        fsv.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String currentQuery) {
                getLoaderManager().initLoader(10, null, SearchActivity.this);
            }
        });

        //Setting empty ListView when there are no items called from / in the database.
        View emptyView = findViewById(R.id.empty_view_search);
        listView.setEmptyView(emptyView);

        //Setting up an adapter to create a list item for expense data in the Cursor.
        //There is no pet data until the loader finishes so passing in null for the cursor.
        cursorAdapter = new ExpenseCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newExpenseIntent = new Intent(getApplicationContext(), NewExpenseActivity.class);

                //Forming the content URI that represents the specific expense we clicked on.
                //We will append the "ID" onto the content uri.
                Uri currentExpenseUri = ContentUris.withAppendedId(ExpenseContract.ExpenseEntry.CONTENT_URI, id);
                //Setting the URI on the data field of the intent.
                newExpenseIntent.setData(currentExpenseUri);
                Log.d("ADebug", "" + currentExpenseUri);
                startActivity(newExpenseIntent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Defines the projection that specifies the column from the table.
        String[] projection = {
                ExpenseContract.ExpenseEntry._ID,
                ExpenseContract.ExpenseEntry.EXPENSE_TITLE,
                ExpenseContract.ExpenseEntry.EXPENSE_TYPE,
                ExpenseContract.ExpenseEntry.EXPENSE_AMOUNT,
                ExpenseContract.ExpenseEntry.EXPENSE_DATE_TIME
        };

        //This loader will execute the Content Provider's query method on a background thread.
        return new CursorLoader(this,        //Parent activity context
                ExpenseContract.ExpenseEntry.CONTENT_URI,    //Provider content URI to query
                projection,                  //Columns to include into the cursor
                null,                        //No selection clause
                null,                        //No selection arguments
                ExpenseContract.ExpenseEntry._ID + " DESC"); //Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
