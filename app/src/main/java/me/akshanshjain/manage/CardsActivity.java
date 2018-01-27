package me.akshanshjain.manage;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
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
        Getting an instance of the KeyGenerator which will use AES Algorithm for encryption.
         */
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        /*
        KeyGenParameterSpec contains properties about the keys.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGenParameterSpec = new KeyGenParameterSpec.Builder(AndroidKeyStore,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();
        }

        /*
        Encrypting the data.
         */
        try {
            keyGenerator.init(keyGenParameterSpec);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        secretKey = keyGenerator.generateKey();
        try {
            cipher = Cipher.getInstance(AES_MODE);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] iv = cipher.getIV();
        try {
            byte[] encryption = cipher.doFinal("Akshansh".getBytes("UTF-8"));
        } catch (IllegalBlockSizeException | UnsupportedEncodingException | BadPaddingException e) {
            e.printStackTrace();
        }

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
