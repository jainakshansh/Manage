package me.akshanshjain.manage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LandingActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private TextView overViewText, overviewAmount;
    private Button allTransactions, allCards;
    private TextView monthlyText, monthlyAmount;
    private TextView incomeHeader, incomeAmount, expenseHeader, expenseAmount;

    private Typeface quicksand_bold, quicksand_medium;

    private static final int REQUEST_PERMISSIONS = 100;

    /*
    These variables are for requesting permissions at run-time.
     */
    private static final int PERMISSION_CALLBACK_CONSTANT = 9;
    private static final int REQUEST_PERMISSION_SETTINGS = 7;
    private String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //Initialising all the views from the XML to style and add listeners to them.
        initViews();

        /*
        Reading the permission status stored in the shared preferences.
         */
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
    }

    private void initViews() {
        //Adding the fonts to the activity from the assets.
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");

        /*
        Referencing views from the XML and styling them in the layout.
         */

        //Checks for permissions and opens the activity if provided.
        fab = findViewById(R.id.fab_landing);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCodeLogic();
            }
        });

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

    private void permissionCodeLogic() {
        if (ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.CAMERA)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(LandingActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA,
                                                Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        REQUEST_PERMISSIONS);
            }
        } else {
            /*
            Calling the function to be performed if the permissions have already been provided.
             */
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        startActivity(new Intent(getApplicationContext(), NewExpenseActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //Checking if all permissions are granted.
            boolean allGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true;
                } else {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, permissionsRequired[2])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Storage Permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LandingActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            if (ActivityCompat.checkSelfPermission(LandingActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //After getting the permissions.
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(LandingActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                /*
                After getting the permissions.
                 */
                proceedAfterPermission();
            }
        }
    }
}
