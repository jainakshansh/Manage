package me.akshanshjain.manage;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.akshanshjain.manage.Databases.CardsActivity;

public class LandingActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private TextView overViewText, overviewAmount;
    private Button allTransactions, allCards;
    private TextView monthlyText, monthlyAmount;
    private TextView incomeHeader, incomeAmount, expenseHeader, expenseAmount;

    private Typeface quicksand_bold, quicksand_medium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //Initialising all the views from the XML to style and add listeners to them.
        initViews();
    }

    private void initViews() {
        //Adding the fonts to the activity from the assets.
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");

        //Referencing views from the XML and styling them in the layout.
        fab = findViewById(R.id.fab_landing);
        overViewText = findViewById(R.id.overview_text);
        overViewText.setTypeface(quicksand_bold);
        overviewAmount = findViewById(R.id.overview_amount);
        overviewAmount.setTypeface(quicksand_bold);

        allTransactions = findViewById(R.id.view_all_transactions);
        allTransactions.setTypeface(quicksand_bold);
        allTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TransactionsActivity.class));
            }
        });
        allCards = findViewById(R.id.view_all_cards);
        allCards.setTypeface(quicksand_bold);
        allCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CardsActivity.class));
            }
        });

        monthlyText = findViewById(R.id.monthly_balance_text);
        monthlyText.setTypeface(quicksand_medium);
        monthlyAmount = findViewById(R.id.monthly_balance_amount);
        monthlyAmount.setTypeface(quicksand_bold);

        incomeHeader = findViewById(R.id.income_text_header);
        incomeHeader.setTypeface(quicksand_medium);
        incomeAmount = findViewById(R.id.income_amount);
        incomeAmount.setTypeface(quicksand_bold);
        expenseHeader = findViewById(R.id.expense_text_header);
        expenseHeader.setTypeface(quicksand_medium);
        expenseAmount = findViewById(R.id.expense_amount);
        expenseAmount.setTypeface(quicksand_bold);
    }
}