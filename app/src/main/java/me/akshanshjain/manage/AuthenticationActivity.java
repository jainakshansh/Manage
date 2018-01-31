package me.akshanshjain.manage;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import me.akshanshjain.manage.Utils.FingerprintHandler;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String KEY_NAME = "AKSHANSH_MANAGE";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting up the activity for full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authentication);

        /*
        Fingerprint only available for devices running Android Versions Marshmallow or above.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            /*
            Checking whether the device has a fingerprint sensor.
             */
            if (!fingerprintManager.isHardwareDetected()) {
                Toast.makeText(this, "Your device doesn't support Fingerprint Authentication!", Toast.LENGTH_SHORT).show();
            }

            /*
            Checking whether the user has granted permission for Fingerprint use.
             */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please enable Fingerprint ermission in device Settings.", Toast.LENGTH_SHORT).show();
            }

            /*
            Checking that the user has registered at least one fingerprint.
             */
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(this, "No Fingerprint configured.\n" +
                        "Please register at least one Fingerprint in device Settings!", Toast.LENGTH_SHORT).show();
            }

            /*
            Checking that the lock screen is secured.
             */
            if (!keyguardManager.isKeyguardSecure()) {
                /*
                If the user hasn’t secured their lock screen with a PIN, Password or Pattern, then display the following text.
                */
                Toast.makeText(this, "Please enable Lock Screen Security in your device's Settings!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }

                if (initCipher()) {
                    /*
                    If the Cipher is initialised successfully, then creating the Crypto Object instance.
                     */
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    /*
                    Starting the Fingerprint Handler class which will be responsible for starting the Authentication process.
                     */
                    FingerprintHandler handler = new FingerprintHandler(this);
                    handler.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

    /*
    Creating method that will gain access to the Keystore and generate the encryption key.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            /*
            Obtaining a reference to the Keystore using standard Android KeyStore container identifier.
             */
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generating the key.
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialising an empty KeyStore.
            keyStore.load(null);

            //Initialising the Key Generator.
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    //Configuring this key in a way that user needs to authenticate with fingerprint each time they use it.
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generating the key.
            keyGenerator.generateKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            e.printStackTrace();
            throw new FingerprintException(e);
        }
    }

    /*
    Creating a method which will be used to initialise the cipher.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            //Obtaining Cipher instance and configuring it with the properties required for Fingerprint Authentication.
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            //Returning true if the Cipher has been initialised successfully.
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            //If Cipher initialisation fails.
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException |
                IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to initialise Cipher!", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }
}
