package com.example.android.bookstoreapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.bookstoreapp.R;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);

        String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_NAME));
        final int bookQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));
        String bookPrice = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_PRICE));
        final String id = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));

        Button sellButton = view.findViewById(R.id.sell_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mCurrentBookUri = Uri.withAppendedPath(BookContract.BookEntry.CONTENT_URI, id);
                ContentValues values = new ContentValues();

                if (bookQuantity >= 1){
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity - 1);

                context.getContentResolver().update(mCurrentBookUri, values, null, null);

                }
            }
        });

        nameTextView.setText(bookName);
        quantityTextView.setText("" + bookQuantity);
        priceTextView.setText(bookPrice);
    }
}
