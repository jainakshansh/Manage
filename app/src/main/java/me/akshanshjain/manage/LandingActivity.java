package me.akshanshjain.manage;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LandingActivity extends AppCompatActivity {

    private TextView overviewText, overviewAmount;
    private TextView monthlyBalance, dateSelected;
    private Typeface quicksand_bold, quicksand_medium;

    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        fab = findViewById(R.id.add_expense_landing);
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");

        overviewText = findViewById(R.id.overview_text);
        overviewText.setTypeface(quicksand_bold);
        overviewAmount = findViewById(R.id.overview_amount);
        overviewAmount.setTypeface(quicksand_bold);
        monthlyBalance = findViewById(R.id.monthly_balance_text);
        monthlyBalance.setTypeface(quicksand_medium);
        dateSelected = findViewById(R.id.date_selected);
        dateSelected.setTypeface(quicksand_bold);
        calendar = Calendar.getInstance();

        updateDate();
        dateSelection();
        dateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LandingActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void dateSelection() {
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };
    }

    private void updateDate() {
        String myDateFormat = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myDateFormat, Locale.US);
        dateSelected.setText(simpleDateFormat.format(calendar.getTime()));
    }
}