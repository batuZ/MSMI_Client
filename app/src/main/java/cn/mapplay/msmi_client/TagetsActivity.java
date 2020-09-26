package cn.mapplay.msmi_client;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.mapplay.msmi_client.msmi.MSMI;
import cn.mapplay.msmi_client.msmi.MSMI_User;

public class TagetsActivity extends AppCompatActivity {
    private String activity_type;
    private String group_idenitifier;
    private TextView title, add_btn;
    private EditText editText;
    private RecyclerView listView;
    private MAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taget_activity);
        activity_type = getIntent().getStringExtra("activity_type");
        group_idenitifier = getIntent().getStringExtra("group_idenitifier");
        title = findViewById(R.id.title);
        add_btn = findViewById(R.id.add_user);
        editText = findViewById(R.id.add_user_id);
        listView = findViewById(R.id.user_list);

        title.setText(activity_type);

        adapter = new MAdapter(R.layout.user_item_layout);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(activity_type.equals("好友列表")){
            // 启动后立即获取好友列表
            MSMI.get_friends(listener());
            // 点击进入聊天页面
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MSMI_User user = (MSMI_User) adapter.getItem(position);
                    ChatActivity.show_by_user(TagetsActivity.this, user);
                }
            });
            // 添加好友
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MSMI.add_friend(editText.getText().toString(), listener());
                }
            });
            // 长按删除好友
            adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    MSMI_User user = (MSMI_User) adapter.getItem(position);
                    MSMI.remove_friend(user.identifier, listener());
                    return false;
                }
            });
        }

        if(activity_type.equals("屏蔽列表")){
            // 启动后立即获取屏蔽列表
            MSMI.get_shield(listener());
            // 添加屏蔽
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MSMI.add_shield(editText.getText().toString(), listener());
                }
            });
            // 长按删除屏蔽
            adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    MSMI_User user = (MSMI_User) adapter.getItem(position);
                    MSMI.remove_shield(user.identifier, listener());
                    return false;
                }
            });
        }


    }

    // 请求成功回调对象
    private MSMI.OnRequestBackListener listener() {
        return new MSMI.OnRequestBackListener() {
            @Override
            public void success(List<MSMI_User> users) {
                adapter.setNewData(users);
            }
        };
    }

    // adapter
    class MAdapter extends BaseQuickAdapter<MSMI_User, BaseViewHolder> {
        public MAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, MSMI_User item) {
            TextView textView = helper.itemView.findViewById(R.id.user_name);
            textView.setText(item.name);
        }
    }
}
