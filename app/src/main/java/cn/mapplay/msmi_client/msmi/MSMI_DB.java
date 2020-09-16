package cn.mapplay.msmi_client.msmi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MSMI_DB extends SQLiteOpenHelper {
    public static String MSG = "messages";
    public static String NOTIF = "notifiactions";

    // 创建版本，当前片本
    private static final int CREATE_VERSON = 2001;
    private static final int DB_VERSON = 2001;
    private static MSMI_DB db_helper;

    public static MSMI_DB helper(Context context){
        if (db_helper == null)
            db_helper = new MSMI_DB(context);
        return db_helper;
    }

    public MSMI_DB(@Nullable Context context) {
        super(context, "MSMI_MESSAGE_DB", null, DB_VERSON);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("create table " + MSG + "(_id integer primary key autoincrement, ");
        sBuffer.append("from_id CHAR(64),");
        sBuffer.append("from_name NVARCHAR(64),");
        sBuffer.append("from_avatar VARCHAR(256),");
        sBuffer.append("action CHAR(64),");
        sBuffer.append("time CHAR(64),");
        sBuffer.append("tag_type CHAR(64),");
        sBuffer.append("tag_id CHAR(64),");
        sBuffer.append("content NVARCHAR(64),");
        sBuffer.append("isReaded BOOLEAN);");
        db.execSQL(sBuffer.toString());
        onUpgrade(db, CREATE_VERSON, DB_VERSON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 2001: // update 2001 to 2002
                    break;
            }
        }
    }
}
