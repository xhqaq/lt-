package com.example.controller;
import com.fasterxml.jackson.annotation.JsonProperty;
public class sendInfo {
    @JsonProperty(value = "port")
    private int port;
    @JsonProperty(value = "size")
    private int size;
    @JsonProperty(value = "sendpath")
    private String sendpath;
    @JsonProperty(value = "ip")
    private String ip;
    @JsonProperty(value = "filename")
    private String filename;
    public sendInfo(int port, int size,String sendpath,String filename,String ip) {
        this.port = port;
        this.size=size;
        this.sendpath = sendpath;
        this.filename=filename;
        this.ip=ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port= port;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size= size;
    }

    public String getSendpath() {
        return sendpath;
    }

    public void setSendpath(String sendpath) {
        this.sendpath = sendpath;
    }

    public String getFilename(){return filename;}

    public void setFilename(String filename){this.filename = filename;}

    public String getIp(){return ip;}

    public void setIp(String ip){this.ip = ip;}
}
