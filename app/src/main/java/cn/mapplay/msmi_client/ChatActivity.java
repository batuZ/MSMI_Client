package cn.mapplay.msmi_client;

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

public class ChatActivity extends AppCompatActivity {
    public final static int CHAT_FLAG = 1;
    private TextView title;
    private ListView listView;
    private EditText editText;
    private TextView send_btn;
    private TextView clear_btn;
    private MSMI_Session session;
    private MSMI_Chat_Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        title = findViewById(R.id.title);
        listView = findViewById(R.id.listview);
        editText = findViewById(R.id.edit_text);
        send_btn = findViewById(R.id.send_btn);
        clear_btn = findViewById(R.id.clear_single_btn);
        session = getIntent().getParcelableExtra("session");
        // 设置title
        title.setText(session.session_title);

        if (session.session_type.equals(MSMI.GROUP)) {
            // 群成员列表
            clear_btn.setText("members");
            clear_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, TagetsActivity.class)
                            .putExtra("activity_type", "成员列表")
                            .putExtra("group_idenitifier", session.session_identifier));
                }
            });
        } else {
            // 清空事件
            clear_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MSMI.clear_messages(session.session_identifier);
                    adapter.changeCursor(MSMI.single_list(session.session_identifier));
                    adapter.notifyDataSetChanged();
                }
            });
        }

        // 接收消息监听
        MSMI.setOnMessageChangedListener(new MSMI.OnMessageChangedListener() {
            @Override
            public void message_changed(String s_id) {
                if (s_id.equals(session.session_identifier)) {
                    adapter.changeCursor(MSMI.single_list(session.session_identifier));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // 设置适配器
        adapter = new MSMI_Chat_Adapter(this, MSMI.single_list(session.session_identifier), true);
        listView.setAdapter(adapter);

        // 发送消息事件
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSMI.send_message(session, editText.getText().toString());
                editText.setText(null);
            }
        });


        // 滚动到最后一条
        listView.setSelection(adapter.getCount() - 1);
    }
}
