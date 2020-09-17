package cn.mapplay.msmi_client;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.mapplay.msmi_client.msmi.MSMI;

public class ChatActivity extends AppCompatActivity {
    private String tag_id, tag_name, tag_avatar;
    private TextView title;
    private ListView listView;
    private EditText editText;
    private TextView send_btn;

    private MSMI_Chat_Adapter adapter;

    // 从聊天列表进入，已有会话记录
    public static void show_by_session(Activity activity, long _id) {
        Cursor cursor = MSMI.get_session_by_id(_id);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            show_by_user(activity,
                    cursor.getString(cursor.getColumnIndex("_identifier")),
                    cursor.getString(cursor.getColumnIndex("_title")),
                    cursor.getString(cursor.getColumnIndex("_avatar")));
        }
    }

    // 从其它地方进入，创建会话，需要目标信息
    public static void show_by_user(Activity activity, String _identifier, String _name, String _avatar) {
        activity.startActivityForResult(new Intent(activity, ChatActivity.class)
                .putExtra("tag_id", _identifier)
                .putExtra("tag_name", _name)
                .putExtra("tag_avatar", _avatar), 1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        tag_id = getIntent().getStringExtra("tag_id");
        tag_name = getIntent().getStringExtra("tag_name");
        tag_avatar = getIntent().getStringExtra("tag_avatar");

        title = findViewById(R.id.title);
        listView = findViewById(R.id.listview);
        editText = findViewById(R.id.edit_text);
        send_btn = findViewById(R.id.send_btn);

        title.setText(tag_name);

        MSMI.setOnMessageChangedListener(new MSMI.OnMessageChangedListener() {
            @Override
            public void message_changed(String s_id) {
                if (s_id.equals(tag_id)) {
                    adapter.changeCursor(MSMI.get_message_by_session(tag_id));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter = new MSMI_Chat_Adapter(this, MSMI.get_message_by_session(tag_id), true);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSMI.send_message(tag_id, tag_name, tag_avatar, editText.getText().toString());
            }
        });
    }
}
