package cn.mapplay.msmi_client.msmi;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 定义api和长连接
 */

public interface MSMI_Server {
    String API = "http://39.107.250.142:3000";
    String WS = "ws://39.107.250.142:3000/cable";

    MSMI_Server ser = new Retrofit.Builder()
            .baseUrl(API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient().newBuilder()
                    .connectTimeout(30000, TimeUnit.MILLISECONDS)
                    .readTimeout(30000, TimeUnit.MILLISECONDS)
                    .build())
            .build()
            .create(MSMI_Server.class);
    // 发送单聊消息
    @POST("message/single")
    Call<JsonObject> single_message(@Header("msmi_token") String a, @Query("user_id") String b, @Query("content") String c);
    // 发送群聊消息
    @POST("message/group")
    Call<JsonObject> group_message(@Header("msmi_token") String a, @Query("group_id") String b, @Query("content") String c);

    // 获取好友列表
    @GET("/friends")
    Call<JsonObject> get_friends(@Header("msmi_token") String a);
    // 添加好友
    @POST("/friends")
    Call<JsonObject> add_friends(@Header("msmi_token") String a, @Query("user_id") String b);
    // 删除好友
    @DELETE("/friends")
    Call<JsonObject> remove_friends(@Header("msmi_token") String a, @Query("user_id") String b);


    // 获取屏蔽列表
    @GET("/shield")
    Call<JsonObject> get_shield(@Header("msmi_token") String a);
    // 添加屏蔽用户
    @POST("/shield")
    Call<JsonObject> add_shield(@Header("msmi_token") String a, @Query("user_id") String b);
    // 移除屏蔽用户
    @DELETE("/shield")
    Call<JsonObject> remove_shield(@Header("msmi_token") String a, @Query("user_id") String b);


    // 获取群列表
    @GET("/groups")
    Call<JsonObject> get_groups(@Header("msmi_token") String a);
    // 创建群
    @POST("/group")
    Call<JsonObject> create_group(@Header("msmi_token") String a, @Query("group_name") String b, @Query("group_icon") String c, @Query("members[]") String... e);
    // 解散群
    @DELETE("/group")
    Call<JsonObject> dismiss_group(@Header("msmi_token") String a, @Query("group_id") String b);


    // 获取群成员列表
    @GET("/members")
    Call<JsonObject> get_members(@Header("msmi_token") String a, @Query("group_id") String b);
    // 添加成员
    @POST("/members")
    Call<JsonObject> add_members(@Header("msmi_token") String a, @Query("group_id") String b, @Query("members[]") String... members);
    // 移除成员
    @DELETE("/members")
    Call<JsonObject> remove_members(@Header("msmi_token") String a, @Query("group_id") String b, @Query("members[]") String... members);


    // 申请加入群
    @POST("/join")
    Call<JsonObject> join(@Header("msmi_token") String a, @Query("group_id") String b);
    // 退群
    @DELETE("/quit")
    Call<JsonObject> quit(@Header("msmi_token") String a, @Query("group_id") String b);
}
