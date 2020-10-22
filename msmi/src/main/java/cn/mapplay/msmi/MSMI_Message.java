package cn.mapplay.msmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

/**
 * 消息模型
 */
public class MSMI_Message {
    public long id;
    public long session_id;
    public MSMI_User sender;
    public long send_time;
    public String content_type;
    public String content;
    public String content_file;
    public String content_preview;
    public String information;

    public MSMI_Message(long session_id) {
        this.session_id = session_id;
    }

    public MSMI_Message(Cursor cursor) {
        if (cursor.getCount() > 0) {
            this.id = cursor.getLong(cursor.getColumnIndex("_id"));
            this.session_id = cursor.getLong(cursor.getColumnIndex("_session_id"));
            this.sender = new MSMI_User(
                    cursor.getString(cursor.getColumnIndex("_sender_id")),
                    cursor.getString(cursor.getColumnIndex("_sender_name")),
                    cursor.getString(cursor.getColumnIndex("_sender_avatar"))
            );
            this.send_time = cursor.getLong(cursor.getColumnIndex("_send_time"));
            this.content_type = cursor.getString(cursor.getColumnIndex("_content_type"));
            this.content = cursor.getString(cursor.getColumnIndex("_content"));
            this.content_file = cursor.getString(cursor.getColumnIndex("_file"));
            this.content_preview = cursor.getString(cursor.getColumnIndex("_preview"));
            this.information = cursor.getString(cursor.getColumnIndex("_information"));
        }
    }

    public boolean save(Context context) {
        if (session_id == 0) return false;
        ContentValues values = new ContentValues();
        values.put("_session_id", session_id);
        values.put("_sender_id", sender.identifier);
        values.put("_sender_name", sender.name);
        values.put("_sender_avatar", sender.avatar);
        values.put("_send_time", send_time);
        values.put("_content_type", content_type);
        values.put("_content", content);
        values.put("_file", content_file);
        values.put("_preview", content_preview);
        values.put("_information", information);
        SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
        long single_id = db.insert(MSMI_DB.MESSAGE, null, values);
        db.close();
        return single_id > 0;
    }

    public MSMI_Information get_information() {
        return information == null ? null : new Gson().fromJson(this.information, MSMI_Information.class);
    }
}
