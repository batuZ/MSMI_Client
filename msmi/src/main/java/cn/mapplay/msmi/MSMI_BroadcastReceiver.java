package cn.mapplay.msmi;

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
        MSMI_Message msg = new Gson().fromJson(message, MSMI_Message.class);
        if (msg.content_type.startsWith("image")) {
            session.content = "[图片]";
        } else if (msg.content_type.startsWith("image")) {
            session.content = "[视频]";
        } else if (msg.content_type.startsWith("audio")) {
            session.content = "[音频]";
        } else if (!msg.content_type.startsWith("text")) {
            session.content = "[文件]";
        }
        if (session.save(context)) {
            msg.session_id = session.id;
            // 插入新的消息
            if (msg.save(context)) {
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
