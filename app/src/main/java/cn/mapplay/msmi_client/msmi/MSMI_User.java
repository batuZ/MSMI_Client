package cn.mapplay.msmi_client.msmi;

/**
 * 用户模型，当前用户就放在这里
 */

public class MSMI_User {
    public static MSMI_User current_user;
    public String identifier;
    public String name;
    public String avatar;
    public String token;

    public MSMI_User(String identifier, String name, String avatar) {
        this.identifier = identifier;
        this.name = name;
        this.avatar = avatar;
    }

    public MSMI_Session session() {
        return new MSMI_Session("single_chat", this.identifier, this.name, this.avatar);
    }
}
