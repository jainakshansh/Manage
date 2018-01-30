package me.akshanshjain.manage.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import me.akshanshjain.manage.LandingActivity;

/**
 * Created by Akshansh on 30-01-2018.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    /*
    Implementing the start auth method, which is responsible for starting the fingerprint authentication process.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    /*
    Called when a fatal error has occurred. It provides error code and error message as it's parameters.
     */
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        /*
        Creating the message that will be displayed if an error occurs.
         */
        Toast.makeText(context, "Authentication Error!\n" + errString, Toast.LENGTH_SHORT).show();
    }

    /*
    Called when the fingerprint doesn't match with any of the fingerprints registered on the device.
     */
    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication Failed!", Toast.LENGTH_SHORT).show();
    }

    /*
    Called when a non-fatal error has occurred. This method provides additional information about the error.
    */
    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(context, "Authentication Help!\n" + helpString, Toast.LENGTH_SHORT).show();
    }

    /*
    Called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device.
    */
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        Toast.makeText(context, "Authentication Success!", Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(context.getApplicationContext(), LandingActivity.class));
        ((Activity) context).finish();
    }
}
