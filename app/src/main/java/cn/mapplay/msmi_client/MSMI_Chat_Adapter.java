package cn.mapplay.msmi_client;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.mapplay.msmi_client.msmi.MSMI_Single;
import cn.mapplay.msmi_client.msmi.MSMI_User;

public class MSMI_Chat_Adapter extends CursorAdapter {
    private MSMI_Single single;

    public MSMI_Chat_Adapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.chat_item_layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        single = new MSMI_Single(cursor);
        ImageView other = view.findViewById(R.id.other);
        ImageView self = view.findViewById(R.id.self);
        TextView content = view.findViewById(R.id.chat_content);
        content.setText(single.content);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) content.getLayoutParams();

        if (single.user.identifier.equals(MSMI_User.current_user.identifier)) {
            other.setVisibility(View.INVISIBLE);
            self.setVisibility(View.VISIBLE);
            content.setBackgroundResource(R.drawable.content_self_back);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            other.setVisibility(View.VISIBLE);
            self.setVisibility(View.INVISIBLE);
            content.setBackgroundResource(R.drawable.content_back);
            lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }
}
