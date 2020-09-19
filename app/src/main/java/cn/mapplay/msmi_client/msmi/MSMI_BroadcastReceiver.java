package cn.mapplay.msmi_client.msmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

/**
 * 接收后台消息，塞进数据库，并触刷新事件
 * */

public class MSMI_BroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MSMI_Backservice";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        MSMI_Single single = new Gson().fromJson(message, MSMI_Single.class);
        if (single != null) {
            // 判断类型塞进数据库
            switch (single.message_type) {
                case "single":
                    // 找到对应的会话，没有就根据single创建一个会话
                    MSMI_Session session = new MSMI_Session(context, single.user.identifier);
                    session.title = single.user.name;
                    session.avatar = single.user.avatar;
                    session.sub_title = single.content;
                    session.update_time = single.send_time;
                    single.session_id = session.id;
                    // 插入新的消息
                    if (session.update(context) && single.save(context)) {
                        if (MSMI.getOnMessageChangedListener() != null) {
                            MSMI.getOnMessageChangedListener().message_changed(session.identifier);
                        }
                        if (MSMI.getOnSessionChangedListener() != null) {
                            MSMI.getOnSessionChangedListener().session_changed();
                        }
                        Log.i(TAG, "single: 插入成功");
                    } else {
                        Log.i(TAG, "single: 插入失败");
                    }
                    break;
            }
        }
    }
}
