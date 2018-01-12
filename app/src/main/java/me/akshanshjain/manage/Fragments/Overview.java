package me.akshanshjain.manage.Fragments;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.akshanshjain.manage.R;

/**
 * Created by Akshansh on 06-01-2018.
 */

public class Overview extends Fragment {

    private TextView overviewText, overviewAmount;
    private TextView monthlyBalance, dateSelected;
    private Typeface quicksand_bold, quicksand_medium;

    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;

    public Overview() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating the layout for the current fragment.
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        quicksand_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getContext().getAssets(), "fonts/Quicksand_Medium.ttf");

        overviewText = view.findViewById(R.id.overview_text);
        overviewText.setTypeface(quicksand_bold);
        overviewAmount = view.findViewById(R.id.overview_amount);
        overviewAmount.setTypeface(quicksand_bold);
        monthlyBalance = view.findViewById(R.id.monthly_balance_text);
        monthlyBalance.setTypeface(quicksand_medium);
        dateSelected = view.findViewById(R.id.date_selected);
        dateSelected.setTypeface(quicksand_bold);
        calendar = Calendar.getInstance();

        updateDate();
        dateSelection();
        dateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return view;
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
