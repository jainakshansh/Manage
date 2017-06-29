package com.akshanshjain.manage;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.akshanshjain.manage.Database.ExpenseContract.ExpenseEntry;

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    FloatingActionButton fab;
    NavigationView navigationView;
    ListView expenseListView;

    private static final int EXPENSE_LOADER = 0;
    ExpenseCursorAdapter mCursorAdapter;

    private static final int REQUEST_PERMISSIONS = 100;

    //These variables are for requesting permissions at run-time.
    private static final int PERMISSION_CALLBACK_CONSTANT = 1000;
    private static final int REQUEST_PERMISSION_SETTINGS = 1001;
    String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //Setting the toolbar for the activity.
        toolbar = (Toolbar) findViewById(R.id.toolbar_landing);
        setSupportActionBar(toolbar);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        //Setting up FAB button to open NewExpense Activity.
        fab = (FloatingActionButton) findViewById(R.id.fab_landing);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCode();
            }
        });

        //Configuring drawer layout.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_landing);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        //Configuring navigation view item clicks.
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ListView which will be populated with the Expense data.
        expenseListView = (ListView) findViewById(R.id.expenseListView);

        //Setting empty ListView when there are no items in the database.
        View emptyView = findViewById(R.id.empty_view);
        expenseListView.setEmptyView(emptyView);

        //Setting up an adapter to create a list item for expense data in the Cursor.
        //There is no pet data until the loader finishes so passing in null for the cursor.
        mCursorAdapter = new ExpenseCursorAdapter(this, null);
        expenseListView.setAdapter(mCursorAdapter);

        //Setting up the Item Click Listener
        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newExpenseIntent = new Intent(getApplicationContext(), NewExpenseActivity.class);

                //Forming the content URI that represents the specific expense we clicked on.
                //We will append the "ID" onto the content uri.
                Uri currentExpenseUri = ContentUris.withAppendedId(ExpenseEntry.CONTENT_URI, id);
                //Setting the URI on the data field of the intent.
                newExpenseIntent.setData(currentExpenseUri);
                Log.d("ADebug", "" + currentExpenseUri);
                startActivity(newExpenseIntent);
            }
        });

        //Starting off the loader.
        getLoaderManager().initLoader(EXPENSE_LOADER, null, this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.search_nav:
                        Toast.makeText(LandingActivity.this, "For search results", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.sort_nav:
                        Toast.makeText(LandingActivity.this, "For sorted results", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.bank_details:
                        startActivity(new Intent(getApplicationContext(), BankDetailsActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.contact_developer:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/html");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, "jainakshansh@outlook.com");
                        startActivity(Intent.createChooser(emailIntent, "Send E-mail"));
                        break;
                    case R.id.about_dev:
                        Intent intentLinkedIn = getPackageManager().getLaunchIntentForPackage("com.linkedin.android");
                        if (intentLinkedIn != null) {
                            intentLinkedIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intentLinkedIn.setData(Uri.parse("https://www.linkedin.com/in/jainakshansh/"));
                            startActivity(intentLinkedIn);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/jainakshansh/"));
                            startActivity(browserIntent);
                        }
                        break;
                    case R.id.exit_app:
                        finish();
                        System.exit(0);
                        break;
                }
                return true;
            }
        });
    }

    private void permissionCode() {
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
            //Call whatever you want
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        Intent new_expense_intent = new Intent(LandingActivity.this, NewExpenseActivity.class);
        startActivity(new_expense_intent);
    }

    @Override
    public void onBackPressed() {
        //checking if drawer layout is open when back pressed and closing it first before you can close the app.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //need to handle navigation view item clicks over here.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Defines the projection that specifies the column from the table.
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.EXPENSE_TITLE,
                ExpenseEntry.EXPENSE_TYPE,
                ExpenseEntry.EXPENSE_AMOUNT,
                ExpenseEntry.EXPENSE_DATE_TIME
        };

        //This loader will execute the Content Provider's query method on a background thread.
        return new CursorLoader(this,        //Parent activity context
                ExpenseEntry.CONTENT_URI,    //Provider content URI to query
                projection,                  //Columns to include into the cursor
                null,                        //No selection clause
                null,                        //No selection arguments
                ExpenseEntry._ID + " DESC"); //Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Updating ExpenseCursorAdapter with this new cursor containing updated expense data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted.
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //Checking if all permissions are granted.
            boolean allGranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
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
                //Got Permissions
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(LandingActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }
}