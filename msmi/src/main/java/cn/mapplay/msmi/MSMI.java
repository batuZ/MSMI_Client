package cn.mapplay.msmi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主入口
 */

public class MSMI {
    public static final String SINGLE = "single_chat";
    public static final String GROUP = "group_chat";
    protected static Activity main_activity;
    protected static MSMI_Config config;
    private static final String TAG = "MSMI_Backservice";
    private static OnSessionChangedListener onSessionChangedListener;
    private static OnMessageChangedListener onMessageChangedListener;

    // 登录
    public static void start(
            @NonNull Activity mainActivity,
            @NonNull String host,
            int port,
            boolean https,
            @NonNull String chat_token,
            String current_user_identifier,
            String current_user_name,
            String current_user_avatar,
            int app_icon,
            OnSessionChangedListener listener) {

        MSMI_Config config = new MSMI_Config();
        config.host = host;
        config.port = port;
        config.https = https;

        MSMI_User current_user = new MSMI_User();
        current_user.identifier = current_user_identifier;
        current_user.name = current_user_name;
        current_user.avatar = current_user_avatar;
        current_user.token = chat_token;

        MSMI.start_with_config(mainActivity, config, current_user, app_icon , listener);
    }

    public static void start_with_config(
            @NonNull Activity mainActivity,
            @NonNull MSMI_Config config,
            @NonNull MSMI_User current_user,
            int app_icon,
            OnSessionChangedListener listener) {
        MSMI.main_activity = mainActivity;
        MSMI.config = config;
        MSMI.onSessionChangedListener = listener;
        MSMI_User.current_user = current_user;
        mainActivity.startService(new Intent(mainActivity, MSMI_Backservice.class).putExtra("token", current_user.token).putExtra("icon", app_icon));
    }

    // 补全链接
    public static String root_(String part) {
        return MSMI_Server.API + "\\" + part;
    }

    // 发送消息
    public static void send_message(MSMI_Session session, String text) {
        // 更新session
        session.content = text;
        session.send_time = new Date().getTime();
        session.is_checked = true;

        if (session.save(main_activity)) {
            // 创建一条single记录，塞到库里
            MSMI_Message message = new MSMI_Message(session.id);
            message.sender = MSMI_User.current_user;
            message.send_time = new Date().getTime();
            message.content_type = "text";
            message.content = text;
            // 发起回调，刷UI
            if (message.save(main_activity)) {
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
            if (session.session_type.equals(SINGLE)) {
                MSMI_Server.ser.single_message(MSMI_User.current_user.token, session.session_identifier, text).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (success(response)) {
                            if (!response.body().get("ms_message").equals("OK"))
                                Toast.makeText(main_activity, response.body().get("ms_message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                    }
                });
            } else {
                MSMI_Server.ser.group_message(MSMI_User.current_user.token, session.session_identifier, text).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (success(response)) {
                            if (!response.body().get("ms_message").equals("OK"))
                                Toast.makeText(main_activity, response.body().get("ms_message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        }
    }

    /**
     * 好友
     */
    // 获取好友列表
    public static void get_friends(final OnRequestBackListener listener) {
        MSMI_Server.ser.get_friends(MSMI_User.current_user.token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 添加好友
    public static void add_friend(String tag_id, final OnRequestBackListener listener) {
        MSMI_Server.ser.add_friends(MSMI_User.current_user.token, tag_id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 删除好友
    public static void remove_friend(String tag_id, final OnRequestBackListener listener) {
        MSMI_Server.ser.remove_friends(MSMI_User.current_user.token, tag_id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    /**
     * 屏蔽
     */
    // 获取屏蔽列表
    public static void get_shield(final OnRequestBackListener listener) {
        MSMI_Server.ser.get_shield(MSMI_User.current_user.token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 添加屏蔽用户
    public static void add_shield(String tag_id, final OnRequestBackListener listener) {
        MSMI_Server.ser.add_shield(MSMI_User.current_user.token, tag_id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 删除屏蔽用户
    public static void remove_shield(String tag_id, final OnRequestBackListener listener) {
        MSMI_Server.ser.remove_shield(MSMI_User.current_user.token, tag_id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("users").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    /**
     * 群
     */
    // 获取群列表
    public static void get_group_list(final OnRequestBackListener listener) {
        MSMI_Server.ser.get_groups(MSMI_User.current_user.token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_Group> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("groups").getAsJsonArray(), new TypeToken<List<MSMI_Group>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 创建群
    public static void create_group(String group_name, String icon_url, String[] members, final OnRequestBackListener listener) {
        MSMI_Server.ser.create_group(MSMI_User.current_user.token, group_name, icon_url, members).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_Group> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("groups").getAsJsonArray(), new TypeToken<List<MSMI_Group>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 解散群
    public static void dismiss_group(String identifier, final OnRequestBackListener listener) {
        MSMI_Server.ser.dismiss_group(MSMI_User.current_user.token, identifier).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_Group> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("groups").getAsJsonArray(), new TypeToken<List<MSMI_Group>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    /**
     * 群成员
     */
    // 获取群成员列表
    public static void get_members(String group_identifier, final OnRequestBackListener listener) {
        MSMI_Server.ser.get_members(MSMI_User.current_user.token, group_identifier).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("members").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // 添加群成员
    public static void add_members(String group_identifier, String[] members, final OnRequestBackListener listener) {
        MSMI_Server.ser.add_members(MSMI_User.current_user.token, group_identifier, members).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("members").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // 移除群成员
    public static void remove_members(String group_identifier, String[] members, final OnRequestBackListener listener) {
        MSMI_Server.ser.remove_members(MSMI_User.current_user.token, group_identifier, members).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_User> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("members").getAsJsonArray(), new TypeToken<List<MSMI_User>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    // 加入群
    public static void join_group(String group_identifier, final OnRequestBackListener listener) {
        MSMI_Server.ser.join(MSMI_User.current_user.token, group_identifier).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_Group> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("groups").getAsJsonArray(), new TypeToken<List<MSMI_Group>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // 退出群
    public static void quit_group(String group_identifier, final OnRequestBackListener listener) {
        MSMI_Server.ser.quit(MSMI_User.current_user.token, group_identifier).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (success(response)) {
                    List<MSMI_Group> res = new Gson().fromJson(response.body().get("ms_content").getAsJsonObject().get("groups").getAsJsonArray(), new TypeToken<List<MSMI_Group>>() {
                    }.getType());
                    if (listener != null)
                        listener.success(res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    /**
     * 会话
     */
    // 获取session列表
    public static Cursor session_list() {
        return MSMI_DB.helper(main_activity).getWritableDatabase()
                .query(MSMI_DB.SESSION, null, null, null, null, null, "_update_time DESC");
    }

    public static List<MSMI_Session> session_list(int s) {
        Cursor cursor = MSMI_DB.helper(main_activity).getWritableDatabase()
                .query(MSMI_DB.SESSION, null, null, null, null, null, "_update_time DESC");
        List<MSMI_Session> list = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                list.add(new MSMI_Session(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }

    // 获取指定session中的消息列表
    public static Cursor single_list(String session_identifier) {
        return MSMI_DB.helper(main_activity).getWritableDatabase()
                .rawQuery("SELECT * FROM single INNER JOIN session WHERE session._identifier=? AND single._session_id = session._id ORDER BY single._send_time ASC;", new String[]{session_identifier});
    }

    // 清空session列表
    public static void clear_sessions() {
        MSMI_DB.helper(main_activity).getWritableDatabase().delete(MSMI_DB.SESSION, null, null);
        MSMI_DB.helper(main_activity).getWritableDatabase().close();
    }

    // 清空session中的single列表
    public static void clear_messages(String session_identifier) {
        MSMI_DB.helper(main_activity).getWritableDatabase()
                .execSQL("DELETE FROM single WHERE _session_id IN (SELECT _id FROM session WHERE _identifier=?);", new String[]{session_identifier});
        MSMI_DB.helper(main_activity).getWritableDatabase().close();
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

    /**
     * 监听
     */
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

    public interface OnSessionChangedListener {
        void session_changed();
    }

    public interface OnMessageChangedListener {
        void message_changed(String session_id);
    }

    public interface OnRequestBackListener {
        void success(List res);
    }
}
