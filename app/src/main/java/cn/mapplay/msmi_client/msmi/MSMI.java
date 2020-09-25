package cn.mapplay.msmi_client.msmi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主入口
 */

public class MSMI {
    private static final String TAG = "MSMI_Backservice";
    private static Context _context;
    private static OnSessionChangedListener onSessionChangedListener;
    private static OnMessageChangedListener onMessageChangedListener;

    // 登录
    public static void start_with_user(Context context, MSMI_User user) {
        _context = context;
        MSMI_User.current_user = user;
        context.startService(new Intent(context, MSMI_Backservice.class).putExtra("token", user.token));
    }

    // 发送消息
    public static void send_message(String tag_id, String tag_name, String tag_avatar, String text) {
        // 更新session
        MSMI_Session session = new MSMI_Session();
        session.session_identifier = tag_id;
        session.session_title = tag_name;
        session.session_icon = tag_avatar;
        session.content = text;
        session.send_time = new Date().getTime();
        session.is_checked = true;

        if (session.save(_context)) {
            // 创建一条single记录，塞到库里
            MSMI_Single single = new MSMI_Single(session.id);
            single.sender = MSMI_User.current_user;
            single.send_time = new Date().getTime();
            single.content_type = "text";
            single.content = text;
            // 发起回调，刷UI
            if (single.save(_context)) {
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

            // 消息发送请求
            MSMI_Server.ser.send_message(MSMI_User.current_user.token, tag_id, text).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (success(response)) {
                        // todo 发送成功
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                }
            });
        }
    }

    public static void get_friends(final OnRequestBackListener listener){
        MSMI_Server.ser.get_frends(MSMI_User.current_user.token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(success(response)){
                    MSMI_User.friends = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if(listener!=null)
                        listener.success();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 添加好友
    public static void add_friend(String tag_id, final OnRequestBackListener listener) {
        MSMI_Server.ser.add_friend(MSMI_User.current_user.token, tag_id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    MSMI_User.friends = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if(listener!=null)
                        listener.success();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // 获取session列表
    public static Cursor session_list() {
        return MSMI_DB.helper(_context).getWritableDatabase()
                .query(MSMI_DB.SESSION, null, null, null, null, null, "_update_time DESC");
    }

    // 获取指定session中的消息列表
    public static Cursor single_list(String session_identifier) {
        return MSMI_DB.helper(_context).getWritableDatabase()
                .rawQuery("SELECT * FROM single INNER JOIN session WHERE session._identifier=? AND single._session_id = session._id ORDER BY single._send_time ASC;", new String[]{session_identifier});
    }

    // 清空session列表
    public static void clear_sessions() {
        MSMI_DB.helper(_context).getWritableDatabase().delete(MSMI_DB.SESSION, null, null);
        MSMI_DB.helper(_context).getWritableDatabase().close();
    }

    // 清空session中的single列表
    public static void clear_messages(String session_identifier) {
        MSMI_DB.helper(_context).getWritableDatabase()
                .execSQL("DELETE FROM single WHERE _session_id IN (SELECT _id FROM session WHERE _identifier=?);", new String[]{session_identifier});
        MSMI_DB.helper(_context).getWritableDatabase().close();
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

    public interface OnRequestBackListener{
        void success();
    }
}
