package it.outset.t1_core.models;

public class HostSettings {
    private String protocol;
    private String host;
    private int portNumber;
    private int trackInterval;
    private int sendInterval;

    public HostSettings(String protocol, String host, int portNumber, int trackInterval, int sendInterval) {
        this.protocol = protocol;
        this.host = host;
        this.portNumber = portNumber;
        this.trackInterval = trackInterval;
        this.sendInterval = sendInterval;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPortNumber() {
        if (portNumber < 0 || portNumber > 65535)
            throw new NumberFormatException("Wrong host port number.");
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getTrackInterval() {
        return trackInterval;
    }

    public void setTrackInterval(int trackInterval) {
        this.trackInterval = trackInterval;
    }

    public int getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }
}
