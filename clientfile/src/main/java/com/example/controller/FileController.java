package com.example.controller;


import com.example.transmission.SendThreadpackage;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.io.File;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
@RestController
@RequestMapping("file")
public class FileController {
    public static long starttime=0;
    public static long endtime=0;
    public static int status=2;
    public static Socket clienttest;
    public static int wholenum=0;
    public static long wholesize=0;
    public static int filenum=0;
    public static long filesize=0;
    public static String filenamenow=null;
    public static int shujubao=0;
    public static int dijibao=0;
    public static String address=null;
    Logger logger=Logger.getLogger(FileController.class);
    // String tIdpath="D:\\tId";
    private static String tId=null;
    public static String path(String str){
        int i=str.length();
        if(str.substring(i-1).equals("/")){
            return str;
        }
        else return str+"/";

    }

    public void sendThread(String address, int port, String sendpath, String filename, int size) throws Exception {
        String[] s=new String[1];
        s[0]=filename;
        SendThreadpackage sendThread = new SendThreadpackage(address, port, sendpath, s, size);
        sendThread.start();
    }
    public void sendThreadpackage(String address, int port, String sendpath, int size)throws Exception{
        SendThreadpackage sendThreadpackage =new SendThreadpackage(address,port,sendpath,size);
        sendThreadpackage.start();
    }
    public void sendpackagefile(int port, int size,String sendpath,String ip)throws Exception{
        sendThreadpackage(ip,port,sendpath,size);

    }


    public void sendfile(int port, int size, String sendpath, String filename, String ip) throws Exception {
        sendThread(ip, port, sendpath, filename, size);

    }

    @RequestMapping(value = "orisend", method = RequestMethod.POST)
    public returninfo sendService(@RequestBody sendInfo sendInfo) throws Exception {
        System.out.println("sendService, sendInfo " + sendInfo);
        String pa=sendInfo.getSendpath();
        String pat=path(pa);//检测“/”
        File file1=new File(pat);//linux系统检测"/",括号内改成pat
        String name=pa+sendInfo.getFilename();
        File file2=new File(name);
        //  String filenametemp=tIdpath+tId+".txt";
        //  File file=new File(filenametemp);
        if(!file1.exists()){
            return new returninfo(tId,300,"filepath error",0,0);

        }
        if(!file2.exists()){
            return new returninfo(tId,301,"filename error",0,0);
        }
        long sum=file2.length();
        address=sendInfo.getIp();
        wholenum=1;
        wholesize=sum;
        tId= UUID.randomUUID().toString().replaceAll("-","");
        try {
            clienttest=new Socket(sendInfo.getIp(),sendInfo.getPort());
        }catch (Exception e){
            logger.error("socket error",e);
            return new returninfo(tId,400,"socket error",1,sum);
        }
        sendfile(sendInfo.getPort(), sendInfo.getSize(), sendInfo.getSendpath(), sendInfo.getFilename(), sendInfo.getIp());
        return new returninfo(tId,0,null,1,sum);
    }
    @RequestMapping(value = "oricheck",method = RequestMethod.POST)
    public check checkservice(@RequestBody tIdInfo tId0)throws Exception{

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date startdate = new Date(starttime);
        Date enddate = new Date(endtime);
        int test=tId0.getType();
        if(test==0) {
            return new check(status,address, wholenum, wholesize, filenum, filesize, filenamenow, shujubao, dijibao,startdate.toString(),enddate.toString());
        }
        else if(test==1){
            if (tId0.gettId().equals(tId)){
                return new check(status,address, wholenum, wholesize, filenum, filesize, filenamenow, shujubao, dijibao,startdate.toString(),enddate.toString());
            }else {
                return new check(0,"tId error",0,0,0,0,null,0,0,null,null);
            }
        }
        else {
            return new check(0,"type error",0,0,0,0,null,0,0,null,null);
        }
    }
    @RequestMapping(value = "orisendpackage",method = RequestMethod.POST)
    public returninfo sendpackageService(@RequestBody sendpackageInfo sendpackageInfo)throws Exception {
        System.out.println("sendpackageService, sendpackageInfo " + sendpackageInfo);
        String pa=sendpackageInfo.getSendpath();
        String pat=path(pa);//检测“/”
        File file = new File(pa);//;linux系统检测"/",括号内改成pat
        if (!file.exists()) {
            return new returninfo(tId,300,"filepath error",0,0);
        }
        address=sendpackageInfo.getIp();
        tId= UUID.randomUUID().toString().replaceAll("-","");

        String[] filenames=file.list();
        int num=filenames.length;
        long sum=0;
        for(int i=0;i<num;i++)
        {
            File filetemp=new File(pa+filenames[i]);
            sum+=filetemp.length();
        }
        wholenum=num;
        wholesize=sum;
        try {
            clienttest=new Socket(sendpackageInfo.getIp(),sendpackageInfo.getPort());
        }catch (Exception e){
            logger.error("socket error",e);
            return new returninfo(tId,400,"socket error",num,sum);
        }
        sendpackagefile(sendpackageInfo.getPort(),sendpackageInfo.getSize(),sendpackageInfo.getSendpath(),sendpackageInfo.getIp());

        return new returninfo(tId,0,null,num,sum);




    }

}
