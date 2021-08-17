package com.example.controller;
import com.fasterxml.jackson.annotation.JsonProperty;
public class returninfo {
    @JsonProperty(value = "tId")
    private String tId;
    @JsonProperty(value = "result")
    private int result;
    @JsonProperty(value = "info")
    private String info;
    @JsonProperty(value = "number")
    private int number;
    @JsonProperty(value = "filesize")
    private long filesize;
    public returninfo(String tId, int result,String info,int number,long filesize) {
        this.tId=tId;
        this.result=result;
        this.info=info;
        this.number=number;
        this.filesize=filesize;
    }

}
