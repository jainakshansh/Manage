package me.akshanshjain.manage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import me.akshanshjain.manage.Databases.ExpenseContract.ExpenseEntry;

public class LandingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FloatingActionButton fab;

    private TextView overViewText, overviewAmount;
    private Button allTransactions, allCards;
    private TextView monthlyText, monthlyAmount;
    private TextView incomeHeader, incomeAmount, expenseHeader, expenseAmount;

    private Typeface quicksand_bold, quicksand_medium;

    private static final int REQUEST_PERMISSIONS = 100;
    private static final int LOADER_ID = 81;

    private PieChart pieChart;

    /*
    These variables are for requesting permissions at run-time.
     */
    private static final int PERMISSION_CALLBACK_CONSTANT = 9;
    private static final int REQUEST_PERMISSION_SETTINGS = 7;
    private String[] permissionsRequired = new String[]{
            //Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus, sharedPreferences;
    int appOpened = 1;
    /*
    Permission variables end.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        /*
        Tracking the number of times the app is opened remotely.
         */
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        appOpened = sharedPreferences.getInt("appOpened", 1);
        appOpened++;
        editor.putInt("appOpened", appOpened);
        editor.apply();

        //Initialising all the views from the XML to style and add listeners to them.
        initViews();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getStringExtra("SOURCE").equals("Transaction")) {
                tutorialScreen();
            }
        }

        /*
        Reading the permission status stored in the shared preferences.
         */
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        getLoaderManager().initLoader(LOADER_ID, null, this);
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
                + ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    /*|| ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, Manifest.permission.CAMERA)*/) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(LandingActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                //Manifest.permission.CAMERA,
                                                Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS);
                            }
                        }).setActionTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white))
                        .show();
            } else {
                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                /*Manifest.permission.CAMERA*/},
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
                    /*|| ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, permissionsRequired[2])*/) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Storage Permissions.");
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*
        Defining the projection that specifies the columns from the table.
         */
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.EXPENSE_TITLE,
                ExpenseEntry.EXPENSE_TYPE,
                ExpenseEntry.EXPENSE_AMOUNT,
                ExpenseEntry.EXPENSE_CATEGORY,
                ExpenseEntry.EXPENSE_DATE_TIME,
                ExpenseEntry.EXPENSE_NOTES,
                ExpenseEntry.EXPENSE_LOCATION
        };

        /*
        This loader will execute the Content Provider's query method on a background thread.
         */
        return new CursorLoader(this,
                ExpenseEntry.CONTENT_URI,
                projection,
                null,
                null,
                ExpenseEntry._ID);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        double incomeSum = 0, expenseSum = 0, grandTotal;
        double incomeMonth = 0, expenseMonth = 0, monthTotal;
        if (data != null) {
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                /*
                Retrieving all the expenses and incomes for the total overview amount status.
                 */
                if (data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_TYPE)).equals("Income")) {
                    incomeSum += Double.parseDouble(data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT)));
                } else {
                    expenseSum += Double.parseDouble(data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT)));
                }

                /*
                Retreiving only monthly values for monthly amount status.
                 */
                String date = data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_DATE_TIME));
                int month = Calendar.getInstance().get(Calendar.MONTH);
                if (date.startsWith(String.valueOf(month))) {
                    if (data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_TYPE)).equals("Income")) {
                        incomeMonth += Double.parseDouble(data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT)));
                    } else {
                        expenseMonth += Double.parseDouble(data.getString(data.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT)));
                    }
                }
            }
        }

        /*
        Conditions for all the transactions ever done.
         */
        if (incomeMonth >= expenseMonth) {
            monthTotal = incomeMonth - expenseMonth;
            monthlyAmount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.palmLeaf));
        } else {
            monthTotal = expenseMonth - incomeMonth;
            monthlyAmount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bostonUniRed));
        }

        /*
        Condition for monthly transactions including only current month.
         */
        if (incomeSum >= expenseSum) {
            grandTotal = incomeSum - expenseSum;
            overviewAmount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.palmLeaf));
        } else {
            grandTotal = expenseSum - incomeSum;
            overviewAmount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.bostonUniRed));
        }

        /*
        Updating the UI with the calculated values.
         */
        incomeAmount.setText("₹ " + (String.valueOf(incomeMonth)));
        expenseAmount.setText("₹ " + String.valueOf(expenseMonth));
        monthlyAmount.setText("₹ " + String.valueOf(monthTotal));
        overviewAmount.setText("₹ " + String.valueOf(grandTotal));

        createChart(incomeMonth, expenseMonth, monthTotal);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void createChart(double incomeMonth, double expenseMonth, double monthTotal) {
        pieChart = findViewById(R.id.pie_chart_landing);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        storeData(incomeMonth, expenseMonth, monthTotal);
    }

    private void storeData(double incomeMonth, double expenseMonth, double monthTotal) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        float income = (float) incomeMonth;
        float expense = (float) expenseMonth;
        float balance = income - expense;
        pieEntries.add(new PieEntry(balance, "Balance"));
        pieEntries.add(new PieEntry(expense, "Expense"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.palmLeaf));
        colors.add(ContextCompat.getColor(getApplicationContext(), R.color.bostonUniRed));

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTextSize(18);
        pieData.setValueTypeface(quicksand_bold);

        Description description = new Description();
        description.setText("Monthly Expenses");
        description.setTypeface(quicksand_bold);
        description.setTextSize(13);

        pieChart.setData(pieData);
        pieChart.setCenterText("Total Income: " + incomeMonth);
        pieChart.setCenterTextTypeface(quicksand_bold);
        pieChart.setCenterTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        pieChart.animateX(500);
        pieChart.setDescription(description);

        pieChart.getLegend().setTypeface(quicksand_bold);
        pieChart.getLegend().setTextSize(12);
        pieChart.getLegend().setXOffset(10);
        pieChart.getLegend().setYOffset(20);
        pieChart.getLegend().setDrawInside(true);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }

    private void tutorialScreen() {
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText("Hello! Please click this icon to add new expense!")
                .setShape(ShapeType.CIRCLE)
                .setTarget(fab)
                .setUsageId("tutorial_landing")
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onBackPressed() {
        if (appOpened % 9 == 0) {
            RateDialog rateDialog = new RateDialog(LandingActivity.this);
            rateDialog.show();
        } else {
            finish();
        }
    }
}
