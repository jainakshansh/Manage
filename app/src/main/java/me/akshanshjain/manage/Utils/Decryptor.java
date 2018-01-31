package me.akshanshjain.manage.Utils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by Akshansh on 31-01-2018.
 */
class Decryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;

    Decryptor() throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    String decryptData(String alias, byte[] encryptedData, byte[] encryptionIV)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIV);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    private SecretKey getSecretKey(String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}
