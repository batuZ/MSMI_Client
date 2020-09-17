package cn.mapplay.msmi_client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cn.mapplay.msmi_client.msmi.MSMI;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private MSMI_List_Adapter adapter;

    private String user_id;
    private String user_name;
    private String user_avatar;
    private String chat_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 登录后连接socket
        if (login()) {
            MSMI.setOnSessionChangedListener(new MSMI.OnSessionChangedListener() {
                @Override
                public void session_changed() {
                    adapter.changeCursor(MSMI.message_list());
                    adapter.notifyDataSetChanged();
                }
            });
            MSMI.start_with_token(this, chat_token, user_id, user_name, user_avatar);
        }

        // 把数库中的内容画在视图上
        listView = findViewById(R.id.listview);
        adapter = new MSMI_List_Adapter(this, MSMI.message_list(), true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatActivity.show_by_session(MainActivity.this, id);
            }
        });

        TextView create_btn = findViewById(R.id.create_session_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_session();
            }
        });
    }

    // 登录app-server，验证用户身份，获取用户信息
    private boolean login() {
        // id\name\avatar 由应用服务器维护
        user_id = "Nigulash_ShuFen";
        user_name = "尼古拉斯·淑芬";
        user_avatar = "https://images.12306.com/avatar/img_9983.jpg";
        // chat_token是由应用服务器向mi服务器发送请求创建的
        // 请求：user --[get_token]--> app-server --[app_id,secrit_key,usr_id,name,avatar] mi-server
        // 返回: mi-server --[token]--> app-server --[save&return]--> user
        chat_token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiTmlndWxhc2hfU2h1RmVuIiwidXNlcl9uYW1lIjoi5bC85Y-k5ouJ5pavwrfmt5HoiqwiLCJhdmF0YXJfdXJsIjoiaHR0cHM6Ly9pbWFnZXMuMTIzMDYuY29tL2F2YXRhci9pbWdfOTk4My5qcGciLCJhcHBfaWQiOiJtYXBwbGF5In0.lM8Jh9ntpgbWWCaTJKsuaMYQC7spfUbJ_FWtFg5Euvs";
        return true;
    }

    // 从应用服务器获取到的目标，创建一个新的会话
    private void create_session() {
        String tag_identifier = "Daogelasi_JianGuo";
        String tag_name = "道格拉斯·建国";
        String tag_avatar = "https://images.12306.com/avatar/img_3617.jpg";
        ChatActivity.show_by_user(this, tag_identifier, tag_name, tag_avatar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.changeCursor(MSMI.message_list());
        adapter.notifyDataSetChanged();
    }
}