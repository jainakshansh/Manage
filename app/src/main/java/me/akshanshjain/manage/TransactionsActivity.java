package me.akshanshjain.manage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class TransactionsActivity extends AppCompatActivity {

    private Toolbar toolbar;

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
    }
}
