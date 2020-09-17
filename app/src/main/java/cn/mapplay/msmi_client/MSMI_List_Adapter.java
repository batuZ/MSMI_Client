package cn.mapplay.msmi_client;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MSMI_List_Adapter extends CursorAdapter {
    public MSMI_List_Adapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title,subtitle,date;
        title = view.findViewById(R.id.title);
        subtitle = view.findViewById(R.id.sub_title);
        date = view.findViewById(R.id.date);
        title.setText(cursor.getString(cursor.getColumnIndex("_title")));
        subtitle.setText(cursor.getString(cursor.getColumnIndex("_sub_title")));
        date.setText(""+cursor.getInt(cursor.getColumnIndex("un_read")));
    }
}
