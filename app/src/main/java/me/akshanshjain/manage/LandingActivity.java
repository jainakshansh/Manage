package me.akshanshjain.manage;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

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
    }
}