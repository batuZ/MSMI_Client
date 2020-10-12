package cn.mapplay.msmi_client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cn.mapplay.msmi.MSMI;
import cn.mapplay.msmi.MSMI_Config;
import cn.mapplay.msmi.MSMI_Session;
import cn.mapplay.msmi.MSMI_User;

import static cn.mapplay.msmi_client.ChatActivity.CHAT_FLAG;

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


        // ===================== 登录拿到chat_toekn后连接socket =====================
        String chat_token = "eyJhbGciOiJIUzI1NiJ9.eyJpZGVudGlmaWVyIjoiTmlndWxhc2hfU2h1RmVuIiwibmFtZSI6IuWwvOWPpOaLieaWr8K35reR6IqsIiwiYXZhdGFyIjoiaHR0cHM6Ly9pbWFnZXMuMTIzMDYuY29tL2F2YXRhci9pbWdfOTk4My5qcGciLCJhcHBfaWQiOiJtYXBwbGF5In0.Sb9jexLCx90vCf4lDKb2_ZF4hoT9je89la_btMmi8Sw";

        // id\name\avatar 由应用服务器维护
        // chat_token是应用服务器向mi服务器发送请求创建的
        // 请求：user --[get_token]--> appServer --[app_id,secrit_key,usr_id,name,avatar]--> miServer
        // 返回: miServer --[token]--> appServer --[save & return]--> user
        MSMI_User current_user = new MSMI_User(
                "Nigulash_ShuFen",
                "尼古拉斯·淑芬",
                "https://images.12306.com/avatar/img_9983.jpg");
        current_user.token = chat_token;

        MSMI_Config config = new MSMI_Config();
        config.host = "www.mapplay.cn";
        config.port = 3334;
        config.https = true;

        MSMI.start_with_config(this, config, current_user, new MSMI.OnSessionChangedListener() {
            @Override
            public void session_changed() {
                adapter.changeCursor(MSMI.session_list());
                adapter.notifyDataSetChanged();
            }
        });

        // ========================== OR =================================
//            MSMI.start(
//                    this,
//                    "39.107.250.142",
//                    3000,
//                    true,
//                    "eyJhbGciOiJIUzI1NiJ9.eyJpZGVudGlmaWVyIjoiTmlndWxhc2hfU2h1RmVuIiwibmFtZSI6IuWwvOWPpOaLieaWr8K35reR6IqsIiwiYXZhdGFyIjoiaHR0cHM6Ly9pbWFnZXMuMTIzMDYuY29tL2F2YXRhci9pbWdfOTk4My5qcGciLCJhcHBfaWQiOiJtYXBwbGF5In0.Sb9jexLCx90vCf4lDKb2_ZF4hoT9je89la_btMmi8Sw",
//                    "Nigulash_ShuFen",
//                    "尼古拉斯·淑芬",
//                    "https://images.12306.com/avatar/img_9983.jpg",
//                    new MSMI.OnSessionChangedListener() {
//                        @Override
//                        public void session_changed() {
//                            adapter.changeCursor(MSMI.session_list());
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//            );

        // ===========================================================

        // 把数库中的内容画在视图上
        listView = findViewById(R.id.listview);
        adapter = new MSMI_Session_Adapter(this, MSMI.session_list(), true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MSMI_Session session = new MSMI_Session(MainActivity.this, id);
                startActivityForResult(new Intent(MainActivity.this, ChatActivity.class).putExtra("session", session), CHAT_FLAG);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friends:
                startActivity(new Intent(this, TagetsActivity.class)
                        .putExtra("activity_type", "好友列表"));
                break;
            case R.id.shield:
                startActivity(new Intent(this, TagetsActivity.class)
                        .putExtra("activity_type", "屏蔽列表"));
                break;
            case R.id.groups:
                startActivity(new Intent(this, TagetsActivity.class)
                        .putExtra("activity_type", "群列表"));
                break;
            case R.id.clear_session_btn:
                MSMI.clear_sessions();
                adapter.changeCursor(MSMI.session_list());
                adapter.notifyDataSetChanged();
                break;
        }
    }

    // 从聊天退出来时，刷新一下session_list
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHAT_FLAG) {
            adapter.changeCursor(MSMI.session_list());
            adapter.notifyDataSetChanged();
        }
    }
}