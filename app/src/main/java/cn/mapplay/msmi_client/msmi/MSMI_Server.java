package cn.mapplay.msmi_client.msmi;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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

    @POST("message/single")
    Call<JsonObject> send_message(@Header("msmi_token") String a, @Query("user_id") String b, @Query("content") String c);

    @GET("/friends")
    Call<JsonObject> get_frends(@Header("msmi_token") String a);

    @POST("/friends")
    Call<JsonObject> add_friend(@Header("msmi_token") String a, @Query("user_id") String b);

    @POST("/group")
    Call<JsonObject> create_group(@Header("msmi_token") String a, @Query("group_name") String b, @Query("group_icon") String c, @Query("members") String[] e);
}
