package com.example.transmission;

import com.example.controller.FileController;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;

public class ReceiveThread extends Thread{  
    private int port;
    private String receivepath;
    Logger logger=Logger.getLogger(ReceiveThread.class);
    public ReceiveThread(int port, String receivepath){
        this.port = port; 
        this.receivepath = receivepath;
    }
    public void run(){  // 覆写run()方法，作为线程 的操作主体
    	FileServer fserver = new FileServer(port,receivepath);
		try {
			fserver.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
    }
    public static void main(int port, String receivepath) throws UnknownHostException, IOException {
        ReceiveThread rt = new ReceiveThread(port,receivepath);
        rt.start();
    }
}