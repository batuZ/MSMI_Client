package cn.mapplay.msmi_client.msmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MSMI_Single {
    public long id;
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

    public MSMI_Single(Cursor cursor){
        if (cursor.getCount() > 0) {
            this.id = cursor.getLong(cursor.getColumnIndex("_id"));
            this.session_id = cursor.getLong(cursor.getColumnIndex("_session_id"));
            this.user = new MSMI_User(
                    cursor.getString(cursor.getColumnIndex("_sender_id")),
                    cursor.getString(cursor.getColumnIndex("_sender_name")),
                    cursor.getString(cursor.getColumnIndex("_sender_avatar")),null
            );
            this.send_time = cursor.getLong(cursor.getColumnIndex("_send_time"));
            this.content_type =  cursor.getString(cursor.getColumnIndex("_content_type"));
            this.content =  cursor.getString(cursor.getColumnIndex("_content"));
            this.preview =  cursor.getString(cursor.getColumnIndex("_preview"));
        }
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
