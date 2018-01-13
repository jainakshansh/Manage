package me.akshanshjain.manage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewExpenseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button expenseButton, incomeButton, datePickerButton;
    private AppCompatSpinner categorySpinner;
    private EditText amountInput;

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;

    private boolean isExpense = false;

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

        expenseButton = findViewById(R.id.expense_new_expense);
        incomeButton = findViewById(R.id.income_new_expense);
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
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewExpenseActivity.this,
                        date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        dateLabelUpdate();
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
}
