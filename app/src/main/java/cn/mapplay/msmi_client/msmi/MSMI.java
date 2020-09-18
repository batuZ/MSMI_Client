package cn.mapplay.msmi_client.msmi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
        MSMI_Session session = new MSMI_Session(_context, tag_id);
        session.title = tag_name;
        session.sub_title = text;
        session.avatar = tag_avatar;
        session.update_time = new Date().getTime();

        MSMI_Single single = new MSMI_Single(session.id);
        single.user = _current_user;
        single.send_time = new Date().getTime();
        single.content_type = "text";
        single.content = text;

        if(session.update() && single.save(_context)){
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
