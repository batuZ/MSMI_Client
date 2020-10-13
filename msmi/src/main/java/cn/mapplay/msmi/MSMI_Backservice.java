package cn.mapplay.msmi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * 长连接的后台监听、分类解析
 */

public class MSMI_Backservice extends Service {
    private final String TAG = "MSMI_Backservice";
    private NotificationManager notificationManager;
    private Handler handler = new Handler();
    private String token;
    private InitSocketThread socketThread;
    private int icon;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence app_name = "msmi";
            NotificationChannel channel = new NotificationChannel("PUSH_CHANNEL_ID", app_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        socketThread = new InitSocketThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        token = intent.getStringExtra("token");
        icon = intent.getIntExtra("icon", 0);
        if (socketThread.getState() == Thread.State.NEW) {
            socketThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initSocket() throws UnknownError, IOException {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().header("msmi-token", token).url(MSMI_Server.WS).build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // 订阅 OnlineChannel
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("command", "subscribe");
                JsonObject channel = new JsonObject();
                channel.addProperty("channel", "OnlineChannel");
                jsonObject.addProperty("identifier", channel.toString());
                webSocket.send(jsonObject.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                /// 解析消息
                try {
                    JSONObject jsonObject = new JSONObject(text);

                    // 建立连接的基础内容
                    if (jsonObject.has("type")) {
                        String msg = jsonObject.getString("type");
                        if (msg.equals("welcome")) {
                            Log.i(TAG, "mWebSocket: 通过身份验证, 连接成功！");
                        } else if (msg.equals("confirm_subscription")) {
                            JsonObject obj = new Gson().fromJson(jsonObject.get("identifier") + "", JsonObject.class);
                            Log.i(TAG, "mWebSocket: 成功订阅" + obj.get("channel").getAsString() + "频道！");
                        } else if (msg.equals("ping")) {
                            // 服务器心跳
                        } else {
                            Log.i(TAG, "收到未定义类型的消息: " + text);
                        }
                    }

                    // 业务内容
                    else if (jsonObject.has("identifier")) {
                        String channel = new Gson().fromJson(jsonObject.get("identifier") + "", JsonObject.class).get("channel").getAsString();
                        JSONObject message = jsonObject.getJSONObject("message");
                        // 分频道的意义：目前，单聊不需要回执，群聊和通知都要回执
                        switch (channel) {
                            case "OnlineChannel":
                                online_channel_message(message);
                                break;
                            default:
                                Log.i(TAG, "收到未知频道的消息: " + text);
                        }
                    } else {
                        Log.i(TAG, "onMessage: 收到未知定义的消息");
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "mWebSocket: 解析错误");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.e(TAG, "onFailure: websocket网络或服务器原因，导致连接服务器失败，15秒后尝试重新连接");
                webSocket.cancel();// 清掉以前的连接，等待重试
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "mWebSocket: 重新连接。。。");
                        new InitSocketThread().start();//创建一个新的连接
                    }
                }, 15000); // 15秒后执行
            }
        });
        client.dispatcher().executorService().shutdown();
    }

    // 发起广播，给MS_BroadcastReceiver处理消息
    private void online_channel_message(JSONObject message) throws JSONException {
        dingdong(message);
        sendBroadcast(new Intent(this, MSMI_BroadcastReceiver.class).putExtra("message", message.toString()));
    }

    // 发出提示通知
    private void dingdong(JSONObject message) throws JSONException {
        // 需要广播权限
        // MainActivity 活动时发起通知
        Intent i_main = new Intent(MSMI.main_activity.getPackageName());
        PendingIntent pi = PendingIntent.getActivity(this, 0, i_main, 0);
        Notification notification = new NotificationCompat.Builder(this, "PUSH_CHANNEL_ID")
                .setSmallIcon(icon)
                .setContentTitle("收到新消息")
                .setContentText(message.getString("content"))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
