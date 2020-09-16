package cn.mapplay.msmi_client.msmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class MSMI_BroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        JSONObject json = null;
        String notifi_type = null;
        try {
            json = new JSONObject(message);
            notifi_type = json.getString("notification_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json != null) {
            // 判断类型塞进数据库
//            switch (notifi_type) {
//                case "Interaction":
//                    MS_System_Message.getInstance().add_message(MS_Application.getContext(), json);
//                    break;
//                case "CircleMessages":
//                    MS_System_Message.getInstance().add_circle_message(MS_Application.getContext(), json);
//                    break;
//            }
        }
    }
}
