package cn.mapplay.msmi;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用户模型，当前用户就放在这里
 */

public class MSMI_User {
    public static MSMI_User current_user;
    public String identifier;
    public String name;
    public String avatar;
    public String token;
    /**
     * 0 : 群主
     * 1~10 ：管理员1，其它未定义
     * 时间戳 : 普通成员
     * */
    public long member_type;

    public MSMI_User() { }

    public MSMI_User(String identifier, String name, String avatar) {
        this.identifier = identifier;
        this.name = name;
        this.avatar = avatar;
    }

    public MSMI_Session session() {
        return new MSMI_Session(MSMI.SINGLE, this.identifier, this.name, this.avatar);
    }
}
