package cn.mapplay.msmi;

public class MSMI_Config {
    public String host;
    public int port;
    public boolean https;

    public MSMI_Config() {
    }

    public MSMI_Config(String _host, int _port, boolean _https) {
        this.host = _host;
        this.port = _port;
        this.https = _https;
    }
}
