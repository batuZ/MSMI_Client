package cn.mapplay.msmi_client.msmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 会话模型
 */

public class MSMI_Session {
    public long id;
    public String message_type;
    public String session_identifier;
    public String session_title;
    public String content; // subtitle
    public String session_icon;
    public long send_time;
    public int un_read_number;
    public boolean is_checked;

    public MSMI_Session(){}
    public MSMI_Session(Context context, String _identifier) {
        SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
        Cursor cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{_identifier}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            ContentValues session_values = new ContentValues();
            session_values.put("_identifier", _identifier);
            db.insert(MSMI_DB.SESSION, null, session_values);
            cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{_identifier}, null, null, null);
        }
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            this.id = cursor.getLong(cursor.getColumnIndex("_id"));
            this.session_identifier = cursor.getString(cursor.getColumnIndex("_identifier"));
            this.session_title = cursor.getString(cursor.getColumnIndex("_title"));
            this.content = cursor.getString(cursor.getColumnIndex("_sub_title"));
            this.session_icon = cursor.getString(cursor.getColumnIndex("_avatar"));
            this.send_time = cursor.getLong(cursor.getColumnIndex("_update_time"));
            this.un_read_number = cursor.getInt(cursor.getColumnIndex("un_read"));
            this.is_checked = cursor.getInt(cursor.getColumnIndex("isChecked")) == 1;
        }
        db.close();
    }

    public MSMI_Session(Context context, long _id) {
        SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
        Cursor cursor = db.query(MSMI_DB.SESSION, null, "_id=?", new String[]{_id + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            set_valus(cursor);
        }
        db.close();
    }

    public MSMI_Session(Cursor cursor) {
        set_valus(cursor);
    }

    private void set_valus(Cursor cursor) {
        if (cursor.getCount() > 0) {
            this.id = cursor.getLong(cursor.getColumnIndex("_id"));
            this.session_identifier = cursor.getString(cursor.getColumnIndex("_identifier"));
            this.session_title = cursor.getString(cursor.getColumnIndex("_title"));
            this.content = cursor.getString(cursor.getColumnIndex("_sub_title"));
            this.session_icon = cursor.getString(cursor.getColumnIndex("_avatar"));
            this.send_time = cursor.getLong(cursor.getColumnIndex("_update_time"));
            this.un_read_number = cursor.getInt(cursor.getColumnIndex("un_read"));
            this.is_checked = cursor.getInt(cursor.getColumnIndex("isChecked")) == 1;
        }
    }

    // 不存在创建，存在则update
    public boolean save(Context context) {
        if (this.session_identifier == null || this.session_identifier.length() == 0) return false;

        ContentValues session_values = new ContentValues();
        session_values.put("_title", this.session_title);
        session_values.put("_sub_title", this.content);
        session_values.put("_avatar", this.session_icon);
        session_values.put("_update_time", this.send_time);
        session_values.put("isChecked", this.is_checked);

        SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
        Cursor cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{this.session_identifier}, null, null, null);
        long res = 0;
        if (cursor == null || cursor.getCount() == 0) {
            session_values.put("_identifier", this.session_identifier);
            session_values.put("un_read", this.un_read_number + 1);
            res = this.id = db.insert(MSMI_DB.SESSION, null, session_values);
        } else {
            cursor.moveToFirst();
            this.id = cursor.getLong(cursor.getColumnIndex("_id"));
            this.un_read_number = cursor.getInt(cursor.getColumnIndex("un_read"));
            session_values.put("un_read", this.un_read_number + 1);
            res = db.update(MSMI_DB.SESSION, session_values, "_id=?", new String[]{Long.toString(this.id)});
        }
        db.close();
        return res > 0;
    }

    public boolean update(Context context) {
        SQLiteDatabase db = MSMI_DB.helper(context).getWritableDatabase();
        ContentValues session_values = new ContentValues();
        session_values.put("_title", session_title);
        session_values.put("_sub_title", content);
        session_values.put("_avatar", session_icon);
        session_values.put("_update_time", send_time);
        session_values.put("un_read", un_read_number + 1);
        session_values.put("isChecked", false);
        int update_res = db.update(MSMI_DB.SESSION, session_values, "_id=?", new String[]{Long.toString(this.id)});
        db.close();
        return update_res > 0;
    }
}
