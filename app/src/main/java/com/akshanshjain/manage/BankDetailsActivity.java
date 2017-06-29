package com.akshanshjain.manage;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.support.v7.app.AppCompatActivity;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class BankDetailsActivity extends AppCompatActivity {

    private static final String ALIAS = "Manage_Akshansh";
    private KeyGenerator keyGenerator;
    private KeyGenParameterSpec keyGenParameterSpec;
    private SecretKey secretKey;
    private Cipher cipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);


    }
}
