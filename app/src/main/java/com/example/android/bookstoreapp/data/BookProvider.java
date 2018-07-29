package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    private BookDbHelper mDbHelper;

    private static final String LOG_TAG = BookProvider.class.getSimpleName();

    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final UriMatcher mUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUrimatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        mUrimatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = mUrimatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Can not query unknown URI:" + uri);
        }
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUrimatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;

            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = mUrimatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book needs a name");
        }

        String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Book needs a name");
        }

        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null && quantity < 1) {
            throw new IllegalArgumentException("Book needs a valid quantity");
        }

        String supplier = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Book needs supplier information");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BookEntry.TABLE_NAME, null, values);

            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for" + uri);
                return null;
            }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int bookRowsDeleted;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = mUrimatcher.match(uri);
        switch (match) {
            case BOOKS:
                bookRowsDeleted = database.delete(BookEntry.TABLE_NAME,selection, selectionArgs);
                break;

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                bookRowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (bookRowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return bookRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String [] selectionArgs) {
        final int match = mUrimatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book needs a name");
        }

        String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Book needs a name");
        }

        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null && quantity < 1) {
            throw new IllegalArgumentException("Book needs a valid quantity");
        }

        String supplier = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Book needs supplier information");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int bookRowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
            if (bookRowsUpdated != 0){
                getContext().getContentResolver().notifyChange(uri,null);
            }
    return bookRowsUpdated;
    }
}


