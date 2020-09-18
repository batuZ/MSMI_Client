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
import cn.mapplay.msmi_client.msmi.MSMI_User;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private MSMI_List_Adapter adapter;
    private MSMI_User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 登录后连接socket
        current_user = get_current_user();
        if (current_user.token != null) {
            MSMI.setOnSessionChangedListener(new MSMI.OnSessionChangedListener() {
                @Override
                public void session_changed() {
                    adapter.changeCursor(MSMI.message_list());
                    adapter.notifyDataSetChanged();
                }
            });
            MSMI.start_with_user(this, current_user);
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
        TextView clear_btn = findViewById(R.id.clear_session_btn);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSMI.clear_sessions();
                adapter.changeCursor(MSMI.message_list());
                adapter.notifyDataSetChanged();
            }
        });
    }

    // 登录app-server，验证用户身份，获取用户信息
    private MSMI_User get_current_user() {
        // id\name\avatar 由应用服务器维护
        // chat_token是由应用服务器向mi服务器发送请求创建的
        // 请求：user --[get_token]--> app-server --[app_id,secrit_key,usr_id,name,avatar] mi-server
        // 返回: mi-server --[token]--> app-server --[save&return]--> user
        return new MSMI_User(
                "Nigulash_ShuFen",
                "尼古拉斯·淑芬",
                "https://images.12306.com/avatar/img_9983.jpg",
                "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiTmlndWxhc2hfU2h1RmVuIiwidXNlcl9uYW1lIjoi5bC85Y-k5ouJ5pavwrfmt5HoiqwiLCJhdmF0YXJfdXJsIjoiaHR0cHM6Ly9pbWFnZXMuMTIzMDYuY29tL2F2YXRhci9pbWdfOTk4My5qcGciLCJhcHBfaWQiOiJtYXBwbGF5In0.lM8Jh9ntpgbWWCaTJKsuaMYQC7spfUbJ_FWtFg5Euvs"
        );
    }

    // 从应用服务器获取到的目标，创建一个新的会话
    private void create_session() {
        MSMI_User user = new MSMI_User(
                "Daogelasi_JianGuo",
                "道格拉斯·建国",
                "https://images.12306.com/avatar/img_3617.jpg",
                null
        );
        ChatActivity.show_by_user(this, user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ChatActivity.CHAT_FLAG){
            adapter.changeCursor(MSMI.message_list());
            adapter.notifyDataSetChanged();
        }
    }
}