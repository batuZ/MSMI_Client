package cn.mapplay.msmi_client.msmi;

import android.database.Cursor;

public class MSMI_User {
    public static MSMI_User current_user;
    public String identifier;
    public String name;
    public String avatar;
    public String token;

    public MSMI_User() {
    }

    public MSMI_User(Cursor cursor) {
        this(
                cursor.getString(cursor.getColumnIndex("_sender_id")),
                cursor.getString(cursor.getColumnIndex("_sender_name")),
                cursor.getString(cursor.getColumnIndex("_sender_avatar")),
                null
        );
    }

    public MSMI_User(String identifier, String name, String avatar, String token) {
        this.identifier = identifier;
        this.name = name;
        this.avatar = avatar;
        this.token = token;
    }
}
