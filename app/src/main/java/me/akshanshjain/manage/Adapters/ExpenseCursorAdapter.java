package me.akshanshjain.manage.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by Akshansh on 13-01-2018.
 */

public class ExpenseCursorAdapter extends CursorAdapter {

    /*
    Constructor for the Adapter taking in context and the cursor.
     */
    public ExpenseCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /*
    Inflating a new view and returning it. We do not bind any data at this point.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
