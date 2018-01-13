package me.akshanshjain.manage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class CardsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        /*
        Setting up the toolbar for the activity.
         */
        toolbar = findViewById(R.id.toolbar_cards);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Cards");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
