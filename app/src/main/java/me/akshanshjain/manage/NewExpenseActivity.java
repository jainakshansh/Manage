package me.akshanshjain.manage;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class NewExpenseActivity extends AppCompatActivity {

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
            getSupportActionBar().setTitle("Add Transaction");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
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
        expenseNotes.setTypeface(quicksand_medium);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_expense_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
        }
        return true;
    }

    /*
   Reading the user inputs to save the details to the database.
   Trim function removes any leading/trailing white spaces, if present.
   */
    private void saveExpense() {

        //Getting the type of the transaction.
        String type = "";
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
        Uri newUri = getContentResolver().insert(ExpenseEntry.CONTENT_URI, values);
        if (newUri == null) {
            //Showing a toast message depending on whether or not the insertion was successful.
            Toast.makeText(this, "Failed inserting a new expense!", Toast.LENGTH_SHORT).show();
        } else {
            //Otherwise the insertion was successfull and we can display successful toast.
            Toast.makeText(this, "Insertion of the expense was successful!", Toast.LENGTH_SHORT).show();
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
}
