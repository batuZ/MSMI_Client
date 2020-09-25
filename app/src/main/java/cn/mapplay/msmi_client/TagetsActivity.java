package cn.mapplay.msmi_client;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.JsonObject;

import java.util.List;

import cn.mapplay.msmi_client.msmi.MSMI;
import cn.mapplay.msmi_client.msmi.MSMI_Server;
import cn.mapplay.msmi_client.msmi.MSMI_User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagetsActivity extends AppCompatActivity {
    private String activity_type;
    private TextView title, add_btn;
    private EditText editText;
    private RecyclerView listView;
    private MAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taget_activity);
        title = findViewById(R.id.title);
        add_btn = findViewById(R.id.add_user);
        editText = findViewById(R.id.add_user_id);
        listView = findViewById(R.id.user_list);
        activity_type = getIntent().getStringExtra("activity_type");

        title.setText(activity_type);
        // 添加好友
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSMI.add_friend(editText.getText().toString(), new MSMI.OnRequestBackListener() {
                    @Override
                    public void success() {
                        adapter.setNewData(MSMI_User.friends);
                    }
                });
            }
        });
        adapter = new MAdapter(R.layout.user_item_layout, MSMI_User.friends);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        /** 事件 */

        // 启动后立即获取好友列表
        MSMI.get_friends(new MSMI.OnRequestBackListener() {
            @Override
            public void success() {
                adapter.setNewData(MSMI_User.friends);
            }
        });

        // 点击进入聊天页面
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        // 长按删除
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

                return false;
            }
        });
    }

    class MAdapter extends BaseQuickAdapter<MSMI_User, BaseViewHolder> {


        public MAdapter(int layoutResId, @Nullable List<MSMI_User> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MSMI_User item) {
            TextView textView = helper.itemView.findViewById(R.id.user_name);
            textView.setText(item.name);
        }
    }
}
