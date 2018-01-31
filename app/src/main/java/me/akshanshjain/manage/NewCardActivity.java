package me.akshanshjain.manage;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.crypto.KeyGenerator;

import me.akshanshjain.manage.Adapters.CategorySpinnerAdapter;

public class NewCardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nameOfBank, holderName, holderNumber;
    private Button validThroughDate;

    private AppCompatSpinner cardType;
    private List<String> cardTypeList;
    private CategorySpinnerAdapter spinnerAdapter;

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;

    private TextView disclaimer, validText, cardTypeText;

    private Typeface quicksand_medium, quicksand_bold;

    private KeyGenerator keyGenerator;
    private KeyGenParameterSpec keyGenParameterSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card);

        toolbar = findViewById(R.id.toolbar_new_card);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add New Card");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        dateLabelUpdate();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateLabelUpdate();
            }
        };
        validThroughDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewCardActivity.this,
                        date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initViews() {
        calendar = Calendar.getInstance();

        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");

        /*
        Referencing the views from the XML and styling them.
         */
        nameOfBank = findViewById(R.id.bank_name_input);
        nameOfBank.setTypeface(quicksand_bold);
        holderName = findViewById(R.id.holder_name_input);
        holderName.setTypeface(quicksand_bold);
        holderNumber = findViewById(R.id.holder_number_input);
        holderNumber.setTypeface(quicksand_bold);
        validThroughDate = findViewById(R.id.valid_thru_input);
        validThroughDate.setTypeface(quicksand_medium);

        disclaimer = findViewById(R.id.no_card_detail_disclaimer);
        disclaimer.setTypeface(quicksand_medium);
        validText = findViewById(R.id.valid_thru_text);
        validText.setTypeface(quicksand_medium);
        cardTypeText = findViewById(R.id.card_type_text);
        cardTypeText.setTypeface(quicksand_medium);

        /*
        Creating the card type array list for selection.
         */
        cardTypeList = new ArrayList<>();
        cardTypeList.add("Credit Card");
        cardTypeList.add("Debit Card");
        spinnerAdapter = new CategorySpinnerAdapter(this, cardTypeList);

        cardType = findViewById(R.id.card_type_input);
        cardType.setAdapter(spinnerAdapter);
        cardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dd);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void dateLabelUpdate() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        validThroughDate.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_card_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_card_new:
                if (isFormValid()) {
                    //Need to save the Cards in the Keystore.
                    finish();
                }
                break;
        }
        return true;
    }

    private boolean isFormValid() {
        boolean bankNamePresent, holderNamePresent, holderNumberPresent;

        //Applying validation to the entered data.
        if (TextUtils.isEmpty(nameOfBank.getText().toString())) {
            nameOfBank.setError("Required!");
            bankNamePresent = false;
        } else {
            bankNamePresent = true;
        }
        if (TextUtils.isEmpty(holderName.getText().toString())) {
            holderName.setError("Required!");
            holderNamePresent = false;
        } else {
            holderNamePresent = true;
        }
        if (TextUtils.isEmpty(holderNumber.getText().toString())) {
            holderNumber.setError("Required!");
            holderNumberPresent = false;
        } else {
            holderNumberPresent = true;
        }
        return bankNamePresent && holderNamePresent && holderNumberPresent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
