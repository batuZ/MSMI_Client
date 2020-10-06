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

import com.bumptech.glide.Glide;

import cn.mapplay.msmi_client.msmi.MSMI_Message;
import cn.mapplay.msmi_client.msmi.MSMI_Server;
import cn.mapplay.msmi_client.msmi.MSMI_User;

public class MSMI_Chat_Adapter extends CursorAdapter {
    private MSMI_Message message;

    public MSMI_Chat_Adapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, null);
        MSG_Holder holder = new MSG_Holder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        message = new MSMI_Message(cursor);
        MSG_Holder holder = (MSG_Holder) view.getTag();
        holder.set_user(message.sender);
        // 设置数据
        if (message.content_type.startsWith("text")) {
            holder.setText(message.content);
        } else if (message.content_type.startsWith("image") || message.content_type.startsWith("video")) {
            holder.setImage(context, message.content_preview);
        } else if (message.content_type.startsWith("audio")) {

        } else {

        }
    }

    class MSG_Holder {
        public ImageView other_avatar, self_avatar, img_content;
        public TextView user_name, text_content;

        public MSG_Holder(View view) {
            this.other_avatar = view.findViewById(R.id.other);
            this.self_avatar = view.findViewById(R.id.self);
            this.text_content = view.findViewById(R.id.chat_content);
            this.img_content = view.findViewById(R.id.image_content);
            this.user_name = view.findViewById(R.id.user_name);
        }

        public void set_user(MSMI_User user) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) text_content.getLayoutParams();
            RelativeLayout.LayoutParams ilp = (RelativeLayout.LayoutParams) img_content.getLayoutParams();
            if (user.identifier.equals(MSMI_User.current_user.identifier)) {
                other_avatar.setVisibility(View.INVISIBLE);
                user_name.setVisibility(View.GONE);
                self_avatar.setVisibility(View.VISIBLE);
                text_content.setBackgroundResource(R.drawable.content_self_back);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                ilp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                other_avatar.setVisibility(View.VISIBLE);
                user_name.setVisibility(View.VISIBLE);
                self_avatar.setVisibility(View.INVISIBLE);
                text_content.setBackgroundResource(R.drawable.content_back);
                lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                ilp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        }

        public void setText(String str) {
            this.text_content.setVisibility(View.VISIBLE);
            this.img_content.setVisibility(View.GONE);
            this.text_content.setText(str);
        }

        public void setImage(Context context, String url) {
            this.text_content.setVisibility(View.GONE);
            this.img_content.setVisibility(View.VISIBLE);
            Glide.with(context).load(MSMI_Server.API + "/" + url).into(this.img_content);
        }
    }
}
