package cn.mapplay.msmi_client.msmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

/**
 * 接收后台消息，塞进数据库，并触刷新事件
 */

public class MSMI_BroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MSMI_Backservice";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        MSMI_Session session = new Gson().fromJson(message, MSMI_Session.class);
        if (session.save(context)) {
            MSMI_Message single = new Gson().fromJson(message, MSMI_Message.class);
            single.session_id = session.id;
            // 插入新的消息
            if (single.save(context)) {
                if (MSMI.getOnMessageChangedListener() != null) {
                    MSMI.getOnMessageChangedListener().message_changed(session.session_identifier);
                }
                if (MSMI.getOnSessionChangedListener() != null) {
                    MSMI.getOnSessionChangedListener().session_changed();
                }
                Log.i(TAG, "single: 插入成功");
            } else {
                Log.i(TAG, "single: 插入失败");
            }
        }
    }
}
