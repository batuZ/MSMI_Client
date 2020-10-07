package cn.mapplay.msmi_client;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.mapplay.msmi.MSMI_Session;

public class MSMI_Session_Adapter extends CursorAdapter {
    private MSMI_Session session;

    public MSMI_Session_Adapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.session_item_layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        session = new MSMI_Session(cursor);
        TextView title = view.findViewById(R.id.title);
        TextView subtitle = view.findViewById(R.id.sub_title);
        TextView date = view.findViewById(R.id.date);
        title.setText(session.session_title);
        subtitle.setText(session.content);
        date.setText(session.un_read_number + "");
    }
}
