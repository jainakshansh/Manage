package me.akshanshjain.manage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.akshanshjain.manage.Adapters.CategorySpinnerAdapter;
import me.akshanshjain.manage.Databases.ExpenseContract.ExpenseEntry;

public class NewExpenseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar toolbar;
    private Button expenseButton, incomeButton, datePickerButton;
    private AppCompatSpinner categorySpinner;
    private EditText amountInput, expenseTitle;
    private EditText expenseNotes, expenseLocation;

    private List<String> categoryList;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;

    private boolean isExpense = false;

    private Typeface quicksand_bold, quicksand_medium;

    private Uri currentExpenseUri;
    private boolean expenseAltered = false;
    private static final int EXISTING_EXPENSE_LOADER = 6;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        /*
        Setting up toolbar for the activity.
         */
        toolbar = findViewById(R.id.toolbar_new_expense);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*
        Getting the intent that launched this activity and examining it.
         */
        Intent intent = getIntent();
        currentExpenseUri = intent.getData();

        /*
        If intent DOES NOT contain the expense URI, then we will create a new expense.
        Otherwise we will edit / update the expense.
         */
        if (currentExpenseUri == null) {
            getSupportActionBar().setTitle("Add Transaction");
            invalidateOptionsMenu();
        } else {
            getSupportActionBar().setTitle("Edit Transaction");
            invalidateOptionsMenu();
        }

        getLoaderManager().initLoader(EXISTING_EXPENSE_LOADER, null, this);

        initViews();

        expenseTitle.setOnTouchListener(touchListener);
        incomeButton.setOnTouchListener(touchListener);
        expenseButton.setOnTouchListener(touchListener);
        amountInput.setOnTouchListener(touchListener);
        datePickerButton.setOnTouchListener(touchListener);
        categorySpinner.setOnTouchListener(touchListener);
        expenseLocation.setOnTouchListener(touchListener);
        expenseNotes.setOnTouchListener(touchListener);
    }

    private void initViews() {
        calendar = Calendar.getInstance();

        /*
        Setting up font styles for the activity.
         */
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");

        expenseButton = findViewById(R.id.expense_new_expense);
        expenseButton.setTypeface(quicksand_bold);
        incomeButton = findViewById(R.id.income_new_expense);
        incomeButton.setTypeface(quicksand_bold);
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpense = true;
                expenseTypeLogic();
            }
        });
        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpense = false;
                expenseTypeLogic();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateLabelUpdate();
            }
        };
        datePickerButton = findViewById(R.id.date_picker_new_expense);
        datePickerButton.setTypeface(quicksand_medium);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewExpenseActivity.this,
                        date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        dateLabelUpdate();

        amountInput = findViewById(R.id.expense_amount_input);
        amountInput.setTypeface(quicksand_bold);
        expenseTitle = findViewById(R.id.expense_title_input);
        expenseTitle.setTypeface(quicksand_medium);

        expenseNotes = findViewById(R.id.expense_notes_input);
        expenseNotes.setTypeface(quicksand_medium, Typeface.ITALIC);
        expenseLocation = findViewById(R.id.expense_location_input);
        expenseLocation.setTypeface(quicksand_medium);

        /*
        Initialising and defining Categories for the expenses.
         */
        categoryList = new ArrayList<>();
        categoryList.add("Daily");
        categoryList.add("Education");
        categoryList.add("Entertainment");
        categoryList.add("Fuel");
        categoryList.add("Maintenance");
        categoryList.add("Meals");
        categoryList.add("Office");
        categoryList.add("Personal");
        categoryList.add("Travel");
        categorySpinnerAdapter = new CategorySpinnerAdapter(getApplicationContext(), categoryList);

        categorySpinner = findViewById(R.id.expense_category_input);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dd);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void expenseTypeLogic() {
        if (!isExpense) {
            expenseButton.setBackgroundResource(R.drawable.rounded_button_outline);
            expenseButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            incomeButton.setBackgroundResource(R.drawable.rounded_button_debit);
            incomeButton.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        } else {
            expenseButton.setBackgroundResource(R.drawable.rounded_button_credit);
            expenseButton.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
            incomeButton.setBackgroundResource(R.drawable.rounded_button_outline);
            incomeButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
    }

    private void dateLabelUpdate() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        datePickerButton.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentExpenseUri == null) {
            //This is a new expense, so change the app bar to Add Transaction.
            invalidateOptionsMenu();
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_expense_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //If it is a new expense, we need to hide the Delete menu item.
        if (currentExpenseUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_expense);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_expense_new:
                if (isFormValid()) {
                    saveExpense();
                    finish();
                }
                break;
            case R.id.delete_expense:
                //Popup confirmation dialog for expense deletion.
                showDeleteConfirmationDialog();
                break;
            case R.id.home:
                //If expense hasn't changed, continue with navigating to the parent activity.
                if (!expenseAltered) {
                    //NavUtils.navigateUpFromSameTask(NewExpenseActivity.this);
                    /*
                    NavUtils.navigateUpTo(NewExpenseActivity.this, getParentActivityIntent());
                    return true;
                    */
                    finish();
                }

                /*
                Otherwise if there are unsaved changes, setting up a dialog to warn the user.
                Create a click listener to handle the user confirming that changes should be discarded.
                */
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User has clicked Discard Button, so navigate to parent activity.
                        //NavUtils.navigateUpFromSameTask(NewExpenseActivity.this);
                        NavUtils.navigateUpTo(NewExpenseActivity.this, getParentActivityIntent());
                    }
                };

                //Show a dialog that notifies the user that there are unsaved changes.
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return true;
    }

    /*
   Reading the user inputs to save the details to the database.
   Trim function removes any leading/trailing white spaces, if present.
   */
    private void saveExpense() {

        //Getting the type of the transaction.
        String type;
        if (isExpense) {
            type = "Expense";
        } else {
            type = "Income";
        }

        //Getting the title for the transaction.
        String title = expenseTitle.getText().toString().trim();

        //Getting the amount for the transaction.
        String amount = amountInput.getText().toString().trim();

        //Getting the date for the transaction.
        String date = datePickerButton.getText().toString().trim();

        //Getting the category for the transaction.
        String category = categorySpinner.getSelectedItem().toString().trim();

        //Getting the location for the transaction.
        String location = expenseLocation.getText().toString();

        //Getting the notes for the transaction.
        String notes = expenseNotes.getText().toString();

        //Creating ContentValues object where we use key value pairs for column names and the rows are the attributes of the expense.
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.EXPENSE_TYPE, type);
        values.put(ExpenseEntry.EXPENSE_TITLE, title);
        values.put(ExpenseEntry.EXPENSE_AMOUNT, amount);
        values.put(ExpenseEntry.EXPENSE_DATE_TIME, date);
        values.put(ExpenseEntry.EXPENSE_CATEGORY, category);
        values.put(ExpenseEntry.EXPENSE_LOCATION, location);
        values.put(ExpenseEntry.EXPENSE_NOTES, notes);

        /*
        Inserting a new expense into the provider and returning the content URI for the new expense.
         */
        if (currentExpenseUri == null) {
            Uri newUri = getContentResolver().insert(ExpenseEntry.CONTENT_URI, values);
            if (newUri == null) {
                //Showing a toast message depending on whether or not the insertion was successful.
                Toast.makeText(this, "Failed inserting a new expense!", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise the insertion was successful and we can display successful toast.
                Toast.makeText(this, "Insertion of the expense was successful!", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Otherwise this is an existing expense so we update the expense with URI and pass in new Content Values.
            int rowsAffected = getContentResolver().update(currentExpenseUri, values, null, null);

            //Showing a toast message depending on whether the update was successful or not.
            if (rowsAffected == 0) {
                Toast.makeText(this, "Failed to update the Expense. Please try again!", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the toast was successful and we display successful toast.
                Toast.makeText(this, "Updating the expense successful!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isFormValid() {
        boolean titlePresent;
        boolean amountPresent;
        //Validation rules for if the data has been entered and if correctly entered.
        if (TextUtils.isEmpty(amountInput.getText().toString())) {
            amountInput.setError("Required!");
            amountPresent = false;
        } else {
            amountPresent = true;
        }
        if (TextUtils.isEmpty(expenseTitle.getText().toString())) {
            expenseTitle.setError("Required!");
            titlePresent = false;
        } else {
            titlePresent = true;
        }
        return titlePresent && amountPresent;
    }

    private void showDeleteConfirmationDialog() {
        /*
        Create an alert dialog to help users have a second opinion of deleting the expense.
        Also includes a message, a positive and a negative button.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to permanently delete this expense?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                User has clicked the delete button, so we proceed to deleting the expense.
                 */
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the Alert Dialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    Performing the deleting of expense in the database.
     */
    private void deletePet() {
        if (currentExpenseUri != null) {
            //Calling Content Resolver to delete the expense at the given content URI.
            int rowsDeleted = getContentResolver().delete(currentExpenseUri, null, null);

            //Showing a toast message which tells if the expense was deleted successfully.
            if (rowsDeleted == 0) {
                //If no rows deleted, there was an error.
                Toast.makeText(this, "Failed to delete the expense!", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise deleting was successful, so display confirmation toast.
                Toast.makeText(this, "Expense deletion successful!", Toast.LENGTH_SHORT).show();
            }
        }
        //Closing the activity after the operation.
        finish();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            expenseAltered = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        /*
        Creating an AlertDialog.Builder and setting the message, and click listeners for positive and negative buttons on the dialog.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard Changes and Quit Editing?");
        builder.setPositiveButton("DISCARD", discardButtonClickListener);
        builder.setNegativeButton("KEEP EDITIING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User has clicked keep editing button, so we dismiss the dialog and continue editing the expense.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Creating and showing the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (currentExpenseUri == null) {
            return null;
        }

        //Since the editor shows all the expense attributes, define a projection
        //It contains all columns from the pet table.
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.EXPENSE_TYPE,
                ExpenseEntry.EXPENSE_TITLE,
                ExpenseEntry.EXPENSE_AMOUNT,
                ExpenseEntry.EXPENSE_DATE_TIME,
                ExpenseEntry.EXPENSE_CATEGORY,
                ExpenseEntry.EXPENSE_LOCATION,
                ExpenseEntry.EXPENSE_NOTES
        };

        //This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,
                currentExpenseUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bailing early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        /*
        Proceeding to moving to the first row if the cursor and reading data from it.
        This should be the only row in the result.
        */
        if (cursor.moveToFirst()) {
            //Extracting out data values from the database.
            String title = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_TITLE));
            String type = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_TYPE));
            String amount = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT));
            String date = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_DATE_TIME));
            String category = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_CATEGORY));
            String notes = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_NOTES));
            String location = cursor.getString(cursor.getColumnIndex(ExpenseEntry.EXPENSE_LOCATION));

            //Updating the views in the UI from the extracted data.
            expenseTitle.setText(title);
            amountInput.setText(amount);
            Log.d("ADebug", type);
            if (type.equals(R.string.income)) {
                isExpense = false;
                expenseTypeLogic();
            } else if (type.equals(R.string.expense)) {
                isExpense = true;
                expenseTypeLogic();
            }
            datePickerButton.setText(date);
            categorySpinner.setSelection(categorySpinnerAdapter.getPosition(category));
            expenseNotes.setText(notes);
            expenseLocation.setText(location);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the loader is invalidated, we will clear out all the data from the fields to function as New Transaction activity.
        expenseTitle.setText("");
        isExpense = false;
        expenseTypeLogic();
        amountInput.setText("");
        dateLabelUpdate();
        categorySpinner.setSelection(0);
        expenseNotes.setText("");
        expenseLocation.setText("");
    }

    @Override
    public void onBackPressed() {
        //If the expense hasn't changed, we return to original activity.
        if (!expenseAltered) {
            super.onBackPressed();
            return;
        }

        //Otherwise there are unsaved changes, so we pop up the warning dialog to the user.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User has clicked discard button, so we exit.
                finish();
            }
        };

        //Showing the dialog to the user to notify about the unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
