package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.R;

import org.w3c.dom.Text;

public class BookCursorAdapter extends CursorAdapter {


    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);

        String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_NAME));
        String bookQuantity = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));
        String bookPrice = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_PRICE));

        nameTextView.setText(bookName);
        quantityTextView.setText(bookQuantity);
        priceTextView.setText(bookPrice);
    }
}
