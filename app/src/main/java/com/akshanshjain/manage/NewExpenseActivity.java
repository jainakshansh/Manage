package com.akshanshjain.manage;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akshanshjain.manage.Database.ExpenseContract.ExpenseEntry;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewExpenseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Toolbar toolbar;
    EditText expenseTitle, expenseDate, expenseAmount, expenseCategory, expenseComments;
    Switch expenseType;
    TextView typeText;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;
    ImageView expenseImage;

    Bitmap bitmap;
    Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private Uri mCurrentExpenseUri;
    private boolean mExpenseChanged = false;

    private static final int EXISTING_EXPENSE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        toolbar = (Toolbar) findViewById(R.id.toolbar_new_expense);
        setSupportActionBar(toolbar);

        //Getting the intent that launched this activity and examining it.
        Intent intent = getIntent();
        mCurrentExpenseUri = intent.getData();

        //If intent DOES NOT contain the expense content URI, then we will create a new expense.
        //Otherwise we will edit / update the exisiting expense.
        if (mCurrentExpenseUri == null) {
            getSupportActionBar().setTitle(getString(R.string.addTransaction));
            invalidateOptionsMenu();
        } else {
            getSupportActionBar().setTitle(getString(R.string.editTransaction));
            invalidateOptionsMenu();
        }

        //Initialising a loader to read the expense data from the database
        getLoaderManager().initLoader(EXISTING_EXPENSE_LOADER, null, this);

        //Setting the up button to goto the previous activity or the parent activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewUpdates();
        dateTime();
        updateLabel();

        expenseTitle.setOnTouchListener(mTouchListener);
        expenseType.setOnTouchListener(mTouchListener);
        expenseAmount.setOnTouchListener(mTouchListener);
        expenseDate.setOnTouchListener(mTouchListener);
        expenseCategory.setOnTouchListener(mTouchListener);
        expenseComments.setOnTouchListener(mTouchListener);
        expenseImage.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCurrentExpenseUri == null) {
            //This is a new pet, so change the app bar to Add a Pet
            invalidateOptionsMenu();
        }
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_expense_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If it's a new pet, we need to hide the Delete menu item
        if (mCurrentExpenseUri == null) {
            MenuItem menuItem = menu.findItem(R.id.deleteTransaction);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTransaction:
                saveExpense();
                finish();
                break;
            case R.id.home:
                //If expense hasn't changed, continue with navigating to the parent activity.
                if (!mExpenseChanged) {
                    NavUtils.navigateUpFromSameTask(NewExpenseActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, setting up a dialog to warn the user.
                //Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User has clicked Disard Button, so navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(NewExpenseActivity.this);
                    }
                };

                //Show a dialog that notifies the user that they have unsaved changes.
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.deleteTransaction:
                //Pop-up confirmation dialog for deletion
                showDeleteConfirmationDialog();
        }
        return true;
    }

    private void viewUpdates() {
        /*
        This function initializes the UI components and performs functions on them.
        This function aims to reduces the clutter in the onCreate function.
        Keep in mind to not include all the view updates over here. Those should always be in order of execution.
         */

        expenseType = (Switch) findViewById(R.id.expense_type);
        typeText = (TextView) findViewById(R.id.expense_type_text);
        expenseTitle = (EditText) findViewById(R.id.expense_title);
        expenseDate = (EditText) findViewById(R.id.expense_date);
        expenseAmount = (EditText) findViewById(R.id.expense_amount);
        expenseCategory = (EditText) findViewById(R.id.expense_category);
        expenseComments = (EditText) findViewById(R.id.expense_comments);
        expenseImage = (ImageView) findViewById(R.id.expense_image);

        //Setting the default switch button state as false which will refer to default state as Debit.
        expenseType.setChecked(false);
        typeText.setText(R.string.debit);

        //Updating text when switch button state changed.
        expenseType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    typeText.setText(R.string.credit);
                } else {
                    typeText.setText(R.string.debit);
                }
            }
        });

        expenseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewExpenseActivity.this);
                builder.setMessage("How would you like to pick your transaction image?");
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePicture.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                });
                builder.setNegativeButton("Pick from Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickImage, 1);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");
                    expenseImage.setImageBitmap(bitmap);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    expenseImage.setImageURI(selectedImage);
                }
        }
    }

    private void dateTime() {
        /*
        This function pops up a date picker dialog when Date field is clicked.
         */
        calendar = Calendar.getInstance(java.util.TimeZone.getDefault());

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewExpenseActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        /*
        Specifying Date formats and setting the date from the DatePicker dialog.
         */
        String dateFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.UK);
        expenseDate.setText(sdf.format(calendar.getTime()));
    }

    /*
    Reading the user inputs to save the details to the database.
    Trim function removes any leading/trailing white spaces, if present.
    */
    private void saveExpense() {

        //Getting the type of transaction.
        String typeString = typeText.getText().toString().trim();

        //Getting the title of the transaction.
        String titleString = expenseTitle.getText().toString().trim();

        //Getting the date of the transaction.
        String dateString = expenseDate.getText().toString();

        //Getting the expense amount of the transaction.
        float amountFloat = Float.parseFloat(expenseAmount.getText().toString().trim());

        //Getting the category of the transaction.
        String categoryString = expenseCategory.getText().toString().trim();

        //Getting the comments for the transaction.
        String commentString = expenseComments.getText().toString().trim();

        //Converting the gotten Bitmap image to byte array for storing in the database.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap imageBm = ((BitmapDrawable) expenseImage.getDrawable()).getBitmap();
        imageBm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        //Checking if it's supposed to be a new expense and check if all the fields in the editor are blank.
        if (mCurrentExpenseUri == null && TextUtils.isEmpty(titleString)
                && TextUtils.isEmpty("" + amountFloat)) {
            //Since no fields were modified we can return early without creating a new expense.
            //We don't need to create ContentValues or do ContentProvider operations.
            return;
        }

        //Creating ContentValues object where we use key value pairs for column names and the rows are the attributes of the expense.
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.EXPENSE_TYPE, typeString);
        values.put(ExpenseEntry.EXPENSE_TITLE, titleString);
        values.put(ExpenseEntry.EXPENSE_DATE_TIME, dateString);
        values.put(ExpenseEntry.EXPENSE_CATEGORY, categoryString);
        values.put(ExpenseEntry.EXPENSE_COMMENTS, commentString);
        values.put(ExpenseEntry.EXPENSE_IMAGE, imageBytes);

        if (TextUtils.isEmpty("" + amountFloat)) {
            Toast.makeText(this, "Amount cannot be zero for a transaction!", Toast.LENGTH_SHORT).show();
        }
        values.put(ExpenseEntry.EXPENSE_AMOUNT, amountFloat);

        //Determining if this is a new or existing expense by checking if mCurrentExpenseUri is null or not.
        if (mCurrentExpenseUri == null) {
            //This is a new expense, so insert a new expense into the provider,
            //returning the content URI for the new expense/
            Uri newUri = getContentResolver().insert(ExpenseEntry.CONTENT_URI, values);

            if (newUri == null) {
                //Showing a toast message depending on whether or not the insertion was successful.
                Toast.makeText(this, "Failed inserting a new expense!", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise the insertion was successfull and we can display successful toast.
                Toast.makeText(this, "Insertion of the expense was successful!", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Otherwise this is an existing expense so we update the expense with content URI mCurrentExpenseUri
            //and pass in the new ContentValues.
            int rowsAffected = getContentResolver().update(mCurrentExpenseUri, values, null, null);

            //Showing a toast message depending on whether the update was successful or not.
            if (rowsAffected == 0) {
                Toast.makeText(this, "Failed to update the Expense. Please try again!", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the toast was successful and we display succesful toast.
                Toast.makeText(this, "Updating the expense successful!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mCurrentExpenseUri == null) {
            return null;
        }

        //Since the editor shows all the expense attributes, define a projection
        //It contains all columns from the pet table.
        String[] projection = {
                ExpenseEntry._ID,
                ExpenseEntry.EXPENSE_TITLE,
                ExpenseEntry.EXPENSE_TYPE,
                ExpenseEntry.EXPENSE_DATE_TIME,
                ExpenseEntry.EXPENSE_AMOUNT,
                ExpenseEntry.EXPENSE_CATEGORY,
                ExpenseEntry.EXPENSE_COMMENTS,
                ExpenseEntry.EXPENSE_IMAGE
        };

        //This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,
                mCurrentExpenseUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bailing early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //Proceeding to moving to the first row if the cursor and reading data from it.
        //This should be the only row in the result.
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_TITLE);
            int typeColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_TYPE);
            int dateColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_DATE_TIME);
            int amountColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_AMOUNT);
            int categoryColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_CATEGORY);
            int commentColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_COMMENTS);
            int imageColumnIndex = cursor.getColumnIndex(ExpenseEntry.EXPENSE_IMAGE);

            //Extracting out the data value from the cursor for the given column index.
            String title = cursor.getString(titleColumnIndex);
            String type = cursor.getString(typeColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String amount = cursor.getString(amountColumnIndex);
            String category = cursor.getString(categoryColumnIndex);
            String comments = cursor.getString(commentColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            //Updating the views in the UI from the database.
            expenseTitle.setText(title);
            if (type.equals(R.string.credit)) {
                expenseType.setChecked(true);
            } else {
                expenseType.setChecked(false);
            }
            expenseDate.setText(date);
            expenseAmount.setText(amount);
            expenseCategory.setText(category);
            expenseComments.setText(comments);
            expenseImage.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the loader is invalidated, we will clear out all the data from the fields to function as New Transaction activity.
        expenseTitle.setText("");
        expenseType.setChecked(false);
        expenseAmount.setText("");
        expenseCategory.setText("");
        expenseComments.setText("");
        expenseImage.setImageResource(R.drawable.ic_add_a_photo);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mExpenseChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        //Creating an AlertDialog.Builder and setting the message, and click listeners for positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and Quit Editing?");
        builder.setPositiveButton("DISCARD", discardButtonClickListener);
        builder.setNegativeButton("KEEP EDITIING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User has clicked the keep editing button so we dismiss the dialog and continue editing the expense.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Creating and showing the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //If expense hasn't changed we return to the original activity.
        if (!mExpenseChanged) {
            super.onBackPressed();
            return;
        }

        //Otherwise there are unsaved changes, so we use the dialog to warn the user.
        //We create a click listener to handle user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User has clicked the Discard button so we exit.
                finish();
            }
        };

        //Showing the dialog that there are unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Delete this expense?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the expense.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the expense.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the expense in the database.
     */
    private void deletePet() {
        //Performing the deletion of expense in the database.
        if (mCurrentExpenseUri != null) {
            //Calling ContentResolver to delete the expense at the given content URI.

            int rowsDeleted = getContentResolver().delete(mCurrentExpenseUri, null, null);

            //Showing a toast message which tells if the expense was deleted successfully or no.
            if (rowsDeleted == 0) {
                //If no rows deleted, there was an error.
                Toast.makeText(this, "Failed to Delete the expense!", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise delete was successful and we can display confirmation toast.
                Toast.makeText(this, "Expense deletion successful!", Toast.LENGTH_SHORT).show();
            }
        }
        //Closing the activity after the operation.
        finish();
    }
}
