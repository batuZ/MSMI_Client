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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private MSMI_Session_Adapter adapter;
    private TextView friedns, shield, groups, clear_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        friedns = findViewById(R.id.friends);
        shield = findViewById(R.id.shield);
        groups = findViewById(R.id.groups);
        clear_btn = findViewById(R.id.clear_session_btn);
        friedns.setOnClickListener(this);
        shield.setOnClickListener(this);
        groups.setOnClickListener(this);
        clear_btn.setOnClickListener(this);

        // 登录后连接socket
        MSMI_User.current_user = get_current_user();
        if ( MSMI_User.current_user.token != null) {
            MSMI.setOnSessionChangedListener(new MSMI.OnSessionChangedListener() {
                @Override
                public void session_changed() {
                    adapter.changeCursor(MSMI.session_list());
                    adapter.notifyDataSetChanged();
                }
            });
            MSMI.start_with_user(this,  MSMI_User.current_user);
        }

        // 把数库中的内容画在视图上
        listView = findViewById(R.id.listview);
        adapter = new MSMI_Session_Adapter(this, MSMI.session_list(), true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatActivity.show_by_session(MainActivity.this, id);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.friends:
                startActivity(new Intent(this, TagetsActivity.class)
                        .putExtra("activity_type", "好友列表"));
                break;
            case R.id.shield:
                startActivity(new Intent(this, TagetsActivity.class)
                        .putExtra("activity_type", "屏蔽列表"));
                break;
            case R.id.groups:
//                startActivity(new Intent(this, TagetsActivity.class).putExtra("activity_type", "群列表"));
                startActivity(new Intent(this, TagetsActivity.class)
                        .putExtra("activity_type", "成员列表")
                        .putExtra("group_idenitifier", "group_idenitifier"));
                break;
            case R.id.clear_session_btn:
                MSMI.clear_sessions();
                adapter.changeCursor(MSMI.session_list());
                adapter.notifyDataSetChanged();
                break;
        }
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
                "eyJhbGciOiJIUzI1NiJ9.eyJpZGVudGlmaWVyIjoiTmlndWxhc2hfU2h1RmVuIiwibmFtZSI6IuWwvOWPpOaLieaWr8K35reR6IqsIiwiYXZhdGFyIjoiaHR0cHM6Ly9pbWFnZXMuMTIzMDYuY29tL2F2YXRhci9pbWdfOTk4My5qcGciLCJhcHBfaWQiOiJtYXBwbGF5In0.Sb9jexLCx90vCf4lDKb2_ZF4hoT9je89la_btMmi8Sw"
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

    // 从聊天退出来时，刷新一下session_list
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ChatActivity.CHAT_FLAG){
            adapter.changeCursor(MSMI.session_list());
            adapter.notifyDataSetChanged();
        }
    }


}