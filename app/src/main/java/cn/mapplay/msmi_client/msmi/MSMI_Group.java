package cn.mapplay.msmi_client.msmi;

public class MSMI_Group {
    public String group_id;
    public String group_name;
    public String group_icon;

    public MSMI_Session session() {
        return new MSMI_Session(MSMI.GROUP, this.group_id, this.group_name, this.group_icon);
    }
}
