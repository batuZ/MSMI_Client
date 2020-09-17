package cn.mapplay.msmi_client.msmi;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MSMI_BroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MSMI_Backservice";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        JSONObject json = null;
        String msg_type = null;

        try {
            json = new JSONObject(message);
            msg_type = json.getString("message_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json != null && msg_type != null) {
            // 判断类型塞进数据库
            SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
            switch (msg_type) {
                case "single":
                    try {
                        // 找到对应的会话，没有就创建一个会话
                        String session_identifier = json.getString("sender_id");
                        Cursor session_curser = MSMI.get_session_by_identifier(session_identifier);
                        long s_id = session_curser.getLong(session_curser.getColumnIndex("_id"));
                        // 更新会话信息
                        if (s_id > 0) {
                            ContentValues session_values = new ContentValues();
                            session_values.put("_title", json.getString("sender_name"));
                            session_values.put("_sub_title", json.getString("content"));
                            session_values.put("_avatar", json.getString("sender_avatar"));
                            session_values.put("_update_time", json.getString("send_time"));
                            int un_read = session_curser.getInt(session_curser.getColumnIndex("un_read"));
                            session_values.put("un_read", un_read + 1);
                            session_values.put("isChecked", false);
                            int update_res = db.update(MSMI_DB.SESSION, session_values, "_id=?", new String[]{Long.toString(s_id)});

                            // 插入新的消息
                            if (update_res > 0) {
                                ContentValues values = new ContentValues();
                                values.put("_session_id", s_id);
                                values.put("_sender_id", json.getString("sender_id"));
                                values.put("_sender_name", json.getString("sender_name"));
                                values.put("_sender_avatar", json.getString("sender_avatar"));
                                values.put("_send_time", json.getString("send_time"));
                                values.put("_content_type", json.getString("content_type"));
                                values.put("_content", json.getString("content"));
                                values.put("_preview", json.getString("preview"));
                                long single_id = db.insert(MSMI_DB.SINGLE, null, values);
                                if (single_id > 0) {
                                    if (MSMI.getOnMessageChangedListener()!=null){
                                        MSMI.getOnMessageChangedListener().message_changed(session_identifier);
                                    }
                                    if (MSMI.getOnSessionChangedListener() != null) {
                                        MSMI.getOnSessionChangedListener().session_changed();
                                    }
                                    Log.i(TAG, "single: 插入成功");
                                }else {
                                    Log.i(TAG, "single: 插入失败");
                                }
                            } else {
                                Log.i(TAG, "single: 会话更新失败");
                            }
                        } else {
                            Log.i(TAG, "single: 没有找到会话");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "group":
                    break;
                case "notification":
                    break;
            }
            db.close();
        }
    }
}
