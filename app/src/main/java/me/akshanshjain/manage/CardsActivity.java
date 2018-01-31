package me.akshanshjain.manage;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CardsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView noCards;
    private Button getStarted;

    private Typeface quicksand_bold;

    private static final String AndroidKeyStore = "AkshanshManageStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private KeyGenerator keyGenerator;
    private KeyGenParameterSpec keyGenParameterSpec;
    private SecretKey secretKey;
    private Cipher cipher;

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

        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");

        /*
        Referencing views from the XML and styling them.
         */
        noCards = findViewById(R.id.no_cards_text);
        getStarted = findViewById(R.id.get_started_cards);
        noCards.setTypeface(quicksand_bold);
        getStarted.setTypeface(quicksand_bold);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewCardActivity.class));
            }
        });
    }
}
