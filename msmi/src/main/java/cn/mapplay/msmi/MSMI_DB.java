package cn.mapplay.msmi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 数据库
 * */

public class MSMI_DB extends SQLiteOpenHelper {
    public static String SESSION = "session";
    public static String SINGLE = "single";
    public static String NOTIFICATION = "notifiaction";

    // 创建版本，当前片本
    private static final int DB_VERSON = 2001;
    private static MSMI_DB db_helper;

    public static MSMI_DB helper(Context context) {
        if (db_helper == null)
            db_helper = new MSMI_DB(context);
        return db_helper;
    }

    public MSMI_DB(@Nullable Context context) {
        super(context, "MSMI_DB", null, DB_VERSON);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("create table " + SESSION + "(_id integer primary key autoincrement, ");
        sBuffer.append("_identifier CHAR(64),");
        sBuffer.append("_type CHAR(64),");
        sBuffer.append("_title NVARCHAR(64),");
        sBuffer.append("_sub_title NVARCHAR(64),");
        sBuffer.append("_avatar VARCHAR(256),");
        sBuffer.append("_update_time CHAR(64),");
        sBuffer.append("un_read integer default 0,");
        sBuffer.append("isChecked BOOLEAN);");
        db.execSQL(sBuffer.toString());

        sBuffer = new StringBuffer();
        sBuffer.append("create table " + SINGLE + "(_id integer primary key autoincrement, ");
        sBuffer.append("_session_id integer,");
        sBuffer.append("_sender_id CHAR(64),");
        sBuffer.append("_sender_name NVARCHAR(128),");
        sBuffer.append("_sender_avatar VARCHAR(256),");
        sBuffer.append("_send_time CHAR(64),");
        sBuffer.append("_content_type CHAR(64),");
        sBuffer.append("_content TEXT,");
        sBuffer.append("_file VARCHAR(256),");
        sBuffer.append("_preview VARCHAR(256),");
        sBuffer.append("FOREIGN KEY (_session_id) REFERENCES " + SESSION + "(_id) ON DELETE CASCADE ON UPDATE CASCADE);");
        db.execSQL(sBuffer.toString());
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys = ON;");// 开启外键约束
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
