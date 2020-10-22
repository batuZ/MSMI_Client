package cn.mapplay.msmi;

import com.google.gson.Gson;

public class MSMI_Information {
    public int thumbnailWidth;
    public int thumbnailHeight;
    public int duration;

    public String to_json() {
        return new Gson().toJson(this);
    }
}
