package com.example.controller;
import com.fasterxml.jackson.annotation.JsonProperty;
public class tIdInfo {
    @JsonProperty(value = "tId")
    private String tId;
    @JsonProperty(value = "type")
    private int type;
    public tIdInfo(String tId,int type) {
        this.tId = tId;
        this.type=type;
        //  this.tId1=tId1;
    }
    public String gettId(){return tId;}
    public int getType(){return type;}

}


