package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;

    private String supplierPhoneNumber;

    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            String bookName = cursor.getString(nameColumnIndex);
            String authorName = cursor.getString(authorColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            supplierPhoneNumber = phone;

            mNameEditText.setText(bookName);
            mAuthorEditText.setText(authorName);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText(String.valueOf(""));
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null){
            setTitle(R.string.editor_activity_title_new_book);
            invalidateOptionsMenu();

            Button contactButton = findViewById(R.id.contact_supplier_button);
            contactButton.setVisibility(View.INVISIBLE);

        } else {
            setTitle(R.string.editor_acitvity_title_edit_book);

            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

            Button supplierPhoneButton = findViewById(R.id.contact_supplier_button);
            supplierPhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(supplierPhoneNumber));
                    startActivity(intent);
                }
            });

        }

        mNameEditText = findViewById(R.id.edit_book_name);
        mAuthorEditText = findViewById(R.id.edit_book_author_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_book_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_book_supplier_phone_number);

        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
    }

    private void saveBook(){

      String nameString = mNameEditText.getText().toString().trim();
      String authorString = mAuthorEditText.getText().toString().trim();
      String priceString = mPriceEditText.getText().toString().trim();
      float priceFloat = Float.parseFloat(priceString);
      String quantityString = mQuantityEditText.getText().toString().trim();
      int quantityInt = Integer.parseInt(quantityString);
      String supplierString = mSupplierEditText.getText().toString().trim();
      String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

      if (mCurrentBookUri == null
              && TextUtils.isEmpty(nameString)
              && TextUtils.isEmpty(authorString)
              && TextUtils.isEmpty(priceString)
              && TextUtils.isEmpty(quantityString)
              && TextUtils.isEmpty(supplierString)
              && TextUtils.isEmpty(supplierPhoneString)){
          return;
      }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR_NAME, authorString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceFloat);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)){
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneString);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null){
                Toast.makeText(this, getString(R.string.error_insert_book), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, getString(R.string.ok_insert_book), Toast.LENGTH_LONG).show();
            }
        } else {
            int bookRowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
                if (bookRowsAffected == 0) {
                    Toast.makeText(this, R.string.error_update_book, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.ok_update_book, Toast.LENGTH_LONG).show();
                  }
          }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveBook();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.unsaved_changes_alert_message);
        alertBuilder.setPositiveButton(R.string.quit_editing, discardButtonClickListener);
        alertBuilder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_book_alert_message);
        builder.setPositiveButton(R.string.continue_delete_book, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });

        builder.setNegativeButton(R.string.stop_delete_book, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook(){
        if (mCurrentBookUri != null){
            int bookRowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (bookRowsDeleted == 0){
                Toast.makeText(this, getString(R.string.error_delete_book), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.ok_delete_book), Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}