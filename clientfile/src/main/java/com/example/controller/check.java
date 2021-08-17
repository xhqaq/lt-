package com.example.controller;
import com.fasterxml.jackson.annotation.JsonProperty;
public class check{
    @JsonProperty(value = "status")
    private int status;
    @JsonProperty(value = "address")
    private String address;
    @JsonProperty(value = "wholenum")
    private int wholenum;
    @JsonProperty(value = "wholesize")
    private long wholesize;
    @JsonProperty(value = "filenum")
    private int filenum;
    @JsonProperty(value = "filesize")
    private long filesize;
    @JsonProperty(value = "filenamenow")
    private String filenamenow;
    @JsonProperty(value = "shujubao")
    private int shujubao;
    @JsonProperty(value = "dijibao")
    private int dijibao;
    @JsonProperty(value = "starttime")
    private String starttime;
    @JsonProperty(value = "endtime")
    private String endtime;

    check(int status,String address,int wholenum,long wholesize,int filenum,long filesize,String filenamenow,int shujubao,int dijibao,String starttime,String endtime)
    {
        this.status=status;
        this.address=address;
        this.wholenum=wholenum;
        this.wholesize=wholesize;
        this.filenum=filenum;
        this.filesize=filesize;
        this.filenamenow=filenamenow;
        this.shujubao=shujubao;
        this.dijibao=dijibao;
        this.starttime=starttime;
        this.endtime=endtime;
    }
}