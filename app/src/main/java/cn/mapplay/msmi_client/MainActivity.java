package cn.mapplay.msmi_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.mapplay.msmi_client.msmi.MSMI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MSMI.start_with_token(this, "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiYmF0dV9pZCIsInVzZXJfbmFtZSI6ImJhdHUiLCJhdmF0YXJfdXJsIjoiYmF0dV9hdmF0YXJfdXJsIiwiYXBwX2lkIjoibWFwcGxheSJ9.MfIN3W6L8dX8LhYU_U0l0BfJUgN9VGA2uIGBeluvuhw");
    }
}