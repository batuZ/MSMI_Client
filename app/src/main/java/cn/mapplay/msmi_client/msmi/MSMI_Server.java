package cn.mapplay.msmi_client.msmi;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MSMI_Server {
    String API = "http://39.107.250.142:3000" ;
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

    @POST("message")
    Call<JsonObject> send_message(@Header("mstoken") String a);

}
