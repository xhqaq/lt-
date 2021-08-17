package com.example.transmission;

import com.example.controller.FileController;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class SendThreadpackage extends Thread{
    private String address;
    private int port;
    private String sendpath;
    private String[] filename;
    private int size;
    Logger logger=Logger.getLogger(SendThreadpackage.class);
    public SendThreadpackage(String address, int port, String sendpath, String[] filename, int size){
        this.address = address;
        this.port = port;
        this.sendpath = sendpath;
        this.filename = filename;
        this.size = size;
    }

    public SendThreadpackage(String address, int port, String sendpath, int size){
        this.address = address;
        this.port = port;
        this.sendpath = sendpath;
        File files = new File(sendpath);
        this.filename = files.list();
        this.size = size;
    }

    public void run(){  // 覆写run()方法，作为线程 的操作主体
        try {
            if(filename!=null) {
                FileClientpackage fclient = new FileClientpackage(address, port, sendpath, filename, size);
                fclient.start();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("socketerror",e);

        }
    }

    public static void main(String address,int port,String sendpath,String[] filename,int size) throws UnknownHostException, IOException {
        SendThreadpackage st = new SendThreadpackage(address, port, sendpath, filename, size);
        st.start();
    }
}
