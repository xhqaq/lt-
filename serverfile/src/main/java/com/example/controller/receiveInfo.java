package com.example.controller;
import com.fasterxml.jackson.annotation.JsonProperty;
public class receiveInfo {
    @JsonProperty(value = "port")
    private int port;
    @JsonProperty(value = "receivepath")
    private String receivepath;
    public receiveInfo(int port, String receivepath) {
        this.port = port;
        this.receivepath = receivepath;
    }
    public int getPort() {
        return port;
    }

    public void setPort(int typeId) {
        this.port= port;
    }

    public String getReceivepath() {
        return receivepath;
    }

    public void setReceivepath(String receivepath) {
        this.receivepath = receivepath;
    }

}
