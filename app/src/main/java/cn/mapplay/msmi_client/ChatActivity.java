package cn.mapplay.msmi_client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.mapplay.msmi_client.msmi.MSMI;
import cn.mapplay.msmi_client.msmi.MSMI_Session;
import cn.mapplay.msmi_client.msmi.MSMI_User;

public class ChatActivity extends AppCompatActivity {
    public final static int CHAT_FLAG = 1;
    private final static String USER_ID = "user_id";
    private final static String USER_NAME = "user_name";
    private final static String USER_AVATAR = "user_avatar";

    private TextView title;
    private ListView listView;
    private EditText editText;
    private TextView send_btn;
    private MSMI_User tag_user;
    private MSMI_Chat_Adapter adapter;

    // 从聊天列表进入，已有会话记录
    public static void show_by_session(Activity activity, long id) {
        MSMI_Session session = new MSMI_Session(activity, id);
        if (session.id > 0) {
            activity.startActivityForResult(new Intent(activity, ChatActivity.class)
                    .putExtra(USER_ID, session.identifier)
                    .putExtra(USER_NAME, session.title)
                    .putExtra(USER_AVATAR, session.avatar), CHAT_FLAG);
        }
    }

    // 从其它地方进入，创建会话，需要目标信息
    public static void show_by_user(Activity activity, MSMI_User user) {
        activity.startActivityForResult(new Intent(activity, ChatActivity.class)
                .putExtra(USER_ID, user.identifier)
                .putExtra(USER_NAME, user.name)
                .putExtra(USER_AVATAR, user.avatar), CHAT_FLAG);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        tag_user = new MSMI_User(
                getIntent().getStringExtra(USER_ID),
                getIntent().getStringExtra(USER_NAME),
                getIntent().getStringExtra(USER_AVATAR), null
        );

        title = findViewById(R.id.title);
        listView = findViewById(R.id.listview);
        editText = findViewById(R.id.edit_text);
        send_btn = findViewById(R.id.send_btn);

        title.setText(tag_user.name);

        MSMI.setOnMessageChangedListener(new MSMI.OnMessageChangedListener() {
            @Override
            public void message_changed(String s_id) {
                if (s_id.equals(tag_user.identifier)) {
                    adapter.changeCursor(MSMI.get_message_by_session(tag_user.identifier));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter = new MSMI_Chat_Adapter(this, MSMI.get_message_by_session(tag_user.identifier), true);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSMI.send_message(tag_user.identifier, tag_user.name, tag_user.avatar, editText.getText().toString());
                editText.setText(null);
            }
        });
    }
}
