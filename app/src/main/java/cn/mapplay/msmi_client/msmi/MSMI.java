package cn.mapplay.msmi_client.msmi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class MSMI {
    private static Context _context;
    private static String _token;
    public static void start_with_token(Context context, String token) {
        _context = context;
        _token = token;
        context.startService(new Intent(context, MSMI_Backservice.class).putExtra("token", token));
    }

    public Cursor message_list(){
        Cursor cursor = null;
        return cursor;
    }

    public interface OnMessageListener{
        void message(Cursor cursor);
    }
}
