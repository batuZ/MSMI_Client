package cn.mapplay.msmi_client.msmi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MSMI {
    public static MSMI_User _current_user;

    private static final String TAG = "MSMI_Backservice";
    private static Context _context;
    private static OnSessionChangedListener onSessionChangedListener;
    private static OnMessageChangedListener onMessageChangedListener;

    public static void start_with_user(Context context, MSMI_User user) {
        _context = context;
        _current_user = user;
        context.startService(new Intent(context, MSMI_Backservice.class).putExtra("token", user.token));
    }

    public static void send_message(String tag_id, String tag_name, String tag_avatar, String text) {
        // 找到对应的会话，没有就手动创建一个会话
        Cursor session_curser = get_session_by_identifier(tag_id);
        long s_id = session_curser.getLong(session_curser.getColumnIndex("_id"));
        // 更新会话信息
        if (s_id > 0) {
            ContentValues session_values = new ContentValues();
            session_values.put("_title", tag_name);
            session_values.put("_sub_title", text);
            session_values.put("_avatar", tag_avatar);
            session_values.put("_update_time", new Date().getTime());
            SQLiteDatabase db = MSMI_DB.helper(_context).getWritableDatabase();
            int update_res = db.update(MSMI_DB.SESSION, session_values, "_id=?", new String[]{Long.toString(s_id)});

            // 插入新的消息
            if (update_res > 0) {
                ContentValues values = new ContentValues();
                values.put("_session_id", s_id);
                values.put("_sender_id", _current_user.identifier);
                values.put("_sender_name", _current_user.name);
                values.put("_sender_avatar", _current_user.avatar);
                values.put("_send_time", new Date().getTime());
                values.put("_content_type", "text");
                values.put("_content", text);
                values.put("_preview", "");
                long single_id = db.insert(MSMI_DB.SINGLE, null, values);
                if (single_id > 0) {
                    if (MSMI.getOnMessageChangedListener() != null) {
                        MSMI.getOnMessageChangedListener().message_changed(tag_id);
                    }
                    if (MSMI.getOnSessionChangedListener() != null) {
                        MSMI.getOnSessionChangedListener().session_changed();
                    }
                    Log.i(TAG, "single: 插入成功");
                } else {
                    Log.i(TAG, "single: 插入失败");
                }
            } else {
                Log.i(TAG, "single: 会话更新失败");
            }
            db.close();
        } else {
            Log.i(TAG, "single: 没有找到会话");
        }


        MSMI_Server.ser.send_message(_current_user.token, tag_id, text).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    public static Cursor message_list() {
        return MSMI_DB.helper(_context)
                .getWritableDatabase()
                .query(MSMI_DB.SESSION, null, null, null, null, null, "_update_time DESC");
    }

    public static Cursor get_session_by_id(long id) {
        Cursor cursor = MSMI_DB.helper(_context)
                .getWritableDatabase()
                .query(MSMI_DB.SESSION, null, "_id=?", new String[]{id + ""}, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    // 通过标识获取会话记录，如果没有就创建一个
    public static Cursor get_session_by_identifier(String identifier) {
        SQLiteDatabase db = MSMI_DB.helper(_context).getWritableDatabase();
        Cursor cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{identifier}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            ContentValues session_values = new ContentValues();
            session_values.put("_identifier", identifier);
            db.insert(MSMI_DB.SESSION, null, session_values);
            cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{identifier}, null, null, null);
        }
        cursor.moveToFirst();
        return cursor;
    }

    public static Cursor get_message_by_session(String session_identifier) {
        Cursor cursor = MSMI_DB.helper(_context).getWritableDatabase()
                .rawQuery("SELECT * FROM single INNER JOIN session WHERE session._identifier=? AND single._session_id = session._id ORDER BY single._send_time ASC;", new String[]{session_identifier});
        cursor.moveToFirst();
        return cursor;
    }

    public static void clear_sessions() {
        MSMI_DB.helper(_context).getWritableDatabase().delete(MSMI_DB.SESSION, null, null);
    }

    public static void setOnSessionChangedListener(OnSessionChangedListener listener) {
        onSessionChangedListener = listener;
    }

    public static OnSessionChangedListener getOnSessionChangedListener() {
        return onSessionChangedListener;
    }

    public static void setOnMessageChangedListener(OnMessageChangedListener listener) {
        onMessageChangedListener = listener;
    }

    public static OnMessageChangedListener getOnMessageChangedListener() {
        return onMessageChangedListener;
    }

    // 请求状态判断，打印错误信息
    private static boolean success(Response<JsonObject> response) {
        if (response.code() < 400) {
            JsonObject body = response.body();
            if (body.get("ms_code").getAsInt() == 1000) {
                return true;
            } else {
                Log.e("🦠", String.format("请求失败: %s(%d)", body.get("ms_message").getAsString(), body.get("ms_code").getAsInt()));
            }
        } else {
            String error = "";
            try {
                error = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("🦠", "请求失败:" + " code:" + response.code() + " By: " + response.message());
        }
        return false;
    }

    public interface OnSessionChangedListener {
        void session_changed();
    }

    public interface OnMessageChangedListener {
        void message_changed(String session_id);
    }
}
