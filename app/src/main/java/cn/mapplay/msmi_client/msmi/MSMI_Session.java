package cn.mapplay.msmi_client.msmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 会话模型
 */

public class MSMI_Session implements Parcelable {
    public long id;
    public String session_type;
    public String session_identifier;
    public String session_title;
    public String content; // subtitle
    public String session_icon;
    public long send_time;
    public int un_read_number;
    public boolean is_checked;

    public MSMI_Session(String type, String identifier, String title, String icon) {
        this.session_type = type;
        this.session_identifier = identifier;
        this.session_title = title;
        this.session_icon = icon;
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
            this.session_type = cursor.getString(cursor.getColumnIndex("_type"));
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
        session_values.put("_type", this.session_type);
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

    /**
     * ======================== Parcelable =========================
     */
    public static final Creator<MSMI_Session> CREATOR = new Creator<MSMI_Session>() {
        @Override
        public MSMI_Session createFromParcel(Parcel in) {
            return new MSMI_Session(in);
        }

        @Override
        public MSMI_Session[] newArray(int size) {
            return new MSMI_Session[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(session_type);
        dest.writeString(session_identifier);
        dest.writeString(session_title);
        dest.writeString(content);
        dest.writeString(session_icon);
        dest.writeLong(send_time);
        dest.writeInt(un_read_number);
        dest.writeByte((byte) (is_checked ? 1 : 0));
    }

    protected MSMI_Session(Parcel in) {
        id = in.readLong();
        session_type = in.readString();
        session_identifier = in.readString();
        session_title = in.readString();
        content = in.readString();
        session_icon = in.readString();
        send_time = in.readLong();
        un_read_number = in.readInt();
        is_checked = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
