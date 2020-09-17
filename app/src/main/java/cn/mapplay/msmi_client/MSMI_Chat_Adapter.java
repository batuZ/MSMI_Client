package cn.mapplay.msmi_client;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MSMI_Chat_Adapter extends CursorAdapter {
    public MSMI_Chat_Adapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (cursor.getString(cursor.getColumnIndex("_sender_id")).equals("")) {
            return  LayoutInflater.from(context).inflate(R.layout.chat_item_self_layout, null);
        } else {
            return  LayoutInflater.from(context).inflate(R.layout.chat_item_layout, null);
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView content = view.findViewById(R.id.chat_content);
        content.setText(cursor.getString(cursor.getColumnIndex("_content")));
    }
}
