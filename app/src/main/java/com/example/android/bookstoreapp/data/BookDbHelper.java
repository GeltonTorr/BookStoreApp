package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookstore.db";
    private static final String TEXT = "TEXT";
    private static final String SPACE = " ";
    private static final String COMMA_SEP = ",";
    private static final String INTEGER = "INTEGER";
    private static final String NOT_NULL = "NOT NULL";
    private static final String REAL = "REAL";
    private static final String SQL_CREATE_PETS_TABLE =
            "CREATE TABLE " +
                    BookEntry.TABLE_NAME + SPACE +"(" +
                    BookEntry._ID + SPACE + "INTEGER PRIMARY KEY AUTOINCREMENT," + SPACE +
                    BookEntry.COLUMN_BOOK_NAME + SPACE +TEXT + SPACE + NOT_NULL + COMMA_SEP + SPACE +
                    BookEntry.COLUMN_BOOK_AUTHOR_NAME + SPACE + TEXT + SPACE + NOT_NULL + COMMA_SEP + SPACE +
                    BookEntry.COLUMN_BOOK_PRICE + SPACE + REAL + NOT_NULL + COMMA_SEP + SPACE +
                    BookEntry.COLUMN_BOOK_QUANTITY + SPACE + INTEGER + NOT_NULL + COMMA_SEP + SPACE +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME + SPACE + TEXT + SPACE + NOT_NULL + COMMA_SEP + SPACE +
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + SPACE + TEXT + SPACE + NOT_NULL + SPACE +
                    ");";
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
}
