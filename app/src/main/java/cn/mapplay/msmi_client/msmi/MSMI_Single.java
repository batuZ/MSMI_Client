package cn.mapplay.msmi_client.msmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MSMI_Single {
    public long session_id;
    public String message_type;
    public MSMI_User user;
    public long send_time;
    public String content_type;
    public String content;
    public Object preview;

    public MSMI_Single(long session_id) {
        this.session_id = session_id;
    }

    public boolean save(Context context) {
        if (session_id == 0) return false;
        ContentValues values = new ContentValues();
        values.put("_session_id", session_id);
        values.put("_sender_id", user.identifier);
        values.put("_sender_name", user.name);
        values.put("_sender_avatar", user.avatar);
        values.put("_send_time", send_time);
        values.put("_content_type", content_type);
        values.put("_content", content);
        values.put("_preview", preview + "");
        SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
        long single_id = db.insert(MSMI_DB.SINGLE, null, values);
        db.close();
        return single_id > 0;
    }
}
