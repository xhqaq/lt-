package com.example.controller;
import com.fasterxml.jackson.annotation.JsonProperty;
public class sendpackageInfo {
    @JsonProperty(value = "port")
    private int port;
    @JsonProperty(value = "size")
    private int size;
    @JsonProperty(value = "sendpath")
    private String sendpath;
    @JsonProperty(value = "ip")
    private String ip;
    public sendpackageInfo(int port, int size,String sendpath,String ip) {
        this.port = port;
        this.size=size;
        this.sendpath = sendpath;
        this.ip=ip;
    }
    public int getPort() {
        return port;
    }

    public int getSize() {
        return size;
    }

    public String getSendpath() {
        return sendpath;
    }

    public String getIp(){return ip;}

}
