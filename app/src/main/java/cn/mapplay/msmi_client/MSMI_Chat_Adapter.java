package cn.mapplay.msmi_client;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MSMI_Chat_Adapter extends CursorAdapter {
    public MSMI_Chat_Adapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.chat_item_layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView other = view.findViewById(R.id.other);
        ImageView self = view.findViewById(R.id.self);
        boolean is_self = cursor.getString(cursor.getColumnIndex("_sender_id")).equals("Nigulash_ShuFen");
        other.setVisibility(is_self ? View.INVISIBLE : View.VISIBLE);
        self.setVisibility(is_self ? View.VISIBLE : View.INVISIBLE);
        TextView content = view.findViewById(R.id.chat_content);
        content.setBackgroundResource(is_self ? R.drawable.content_self_back:R.drawable.content_back);
        content.setText(cursor.getString(cursor.getColumnIndex("_content")));
    }
}
