package cn.mapplay.msmi_client.msmi;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MSMI_Session {
    public long id;
    public String identifier;
    public String title;
    public String sub_title;
    public String avatar;
    public long update_time;
    public int un_read_number;
    public boolean is_checked;

    public MSMI_Session(SQLiteDatabase db, String identifier) {
        Cursor cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{identifier}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            ContentValues session_values = new ContentValues();
            session_values.put("_identifier", identifier);
            db.insert(MSMI_DB.SESSION, null, session_values);
            cursor = db.query(MSMI_DB.SESSION, null, "_identifier=?", new String[]{identifier}, null, null, null);
        }
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            this.id = cursor.getLong(cursor.getColumnIndex("_id"));
            this.identifier = cursor.getString(cursor.getColumnIndex("_identifier"));
            this.title = cursor.getString(cursor.getColumnIndex("_title"));
            this.sub_title = cursor.getString(cursor.getColumnIndex("_sub_title"));
            this.avatar = cursor.getString(cursor.getColumnIndex("_avatar"));
            this.update_time = cursor.getLong(cursor.getColumnIndex("_update_time"));
            this.un_read_number = cursor.getInt(cursor.getColumnIndex("un_read"));
            this.is_checked = cursor.getInt(cursor.getColumnIndex("isChecked")) == 1;
        }
    }

    public void update(SQLiteDatabase db) {
        if (this.id > 0) {
            ContentValues session_values = new ContentValues();
            session_values.put("_title", title);
            session_values.put("_sub_title", sub_title);
            session_values.put("_avatar", avatar);
            session_values.put("_update_time", update_time);
            session_values.put("un_read", un_read_number + 1);
            session_values.put("isChecked", false);
            db.update(MSMI_DB.SESSION, session_values, "_id=?", new String[]{Long.toString(this.id)});
        }
    }
}
