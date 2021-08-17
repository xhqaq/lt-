package com.example.transmission;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import com.example.controller.FileController;
import org.apache.log4j.Logger;

public class FileClientpackage {
    private  static int SLEEP=20;

    // Timer timer=new Timer();
    private static long MAX_PACKET_NUM = 1024*1024;
    private static int CLOSE_FLAG = 5555;
    private Socket client;

    private InputStream ins;

    private FileInputStream fis;

    private DataOutputStream dos;

    private DataInputStream dis;
    private String address;

    private int port;

    private int size;
    private String filepath;
    private String[] filename;
    private int filenumber;
    //private boolean completed;
    Logger logger=Logger.getLogger(FileClientpackage.class);
    /**
     * 启动客户端需要指定地址和端口号
     * @throws IOException
     * @throws UnknownHostException
     */
    public FileClientpackage(String address, int port, String filepath, String[] filename, int size) throws UnknownHostException, IOException {
        //this.client = new Socket(address,port);


        //System.out.println(this.client);
        this.client=FileController.clienttest;
        this.address = address;
        this.ins = client.getInputStream();
        this.port = port;
        this.filepath = filepath;
        this.filename = filename;
        this.size = size;
        this.filenumber = filename.length;
       // System.out.println(filename[0]);
        //System.out.println(filename[1]);
//    	System.out.println(filename[2]);
        this.dos = new DataOutputStream(client.getOutputStream());

        this.dis = new DataInputStream(client.getInputStream());

        dos.writeInt(filenumber);
        dos.flush();
        dos.writeInt(size);
        dos.flush();
        //this.completed = false;


    }

    public void start() throws InterruptedException{
        FileController.status=1;
        FileController.starttime=System.currentTimeMillis();
        try {
            for (int i = 0; i<filename.length;i++) {
                System.out.println(i);
                System.out.println(filename[i]);
                //this.completed = false;

                dos.writeInt(i);
                dos.flush();
                FileController.filenum=i+1;
                FileController.filenamenow=filepath+filename[i];
                FileController.filesize=new File(FileController.filenamenow).length();

                sendSingleFile(filepath,filename[i],size);
                FileController.endtime=System.currentTimeMillis();;

            }
            //dos.writeInt(CLOSE_FLAG);
           // dos.flush();
            if(!client.isClosed()){
                client.close();
            }
            FileController.status=2;
        } catch (Exception e) {
            // TODO Auto-generated catch block

            logger.error("sendfileerror",e);
        }
    }

    public void sendSingleFile(String filepath, String name,int size) throws IOException {
        try {
            fis = new FileInputStream(filepath+name);
//       dos = new DataOutputStream(client.getOutputStream());
            dos.writeUTF(name);
            dos.flush();

            //System.setOut(ps);
            // 开始传输文件
            FileController.dijibao=0;
            //
            byte[] data = readFile(filepath+name);
            int totalsize = data.length;

            dos.writeInt(totalsize);
            dos.flush();

            //double devide = totalsize/size;
            int totalnumber = (int)Math.ceil(data.length / (double)size);

            FileController.shujubao=totalnumber;

            dos.writeInt(totalnumber);
            dos.flush();

            int ackno = 0;
            for(int i = 1; i<=totalnumber;i++) {
                FileController.dijibao=i;
                dos.writeInt(i);
                dos.flush();
                if(i==totalnumber)
                {
                    int lastsize=totalsize- (i-1)*size;
                    dos.writeInt(lastsize);
                    dos.flush();
                    dos.write(data,(i - 1) * size,lastsize);
                    dos.flush();
                }
                else {
                    dos.writeInt(size);
                    dos.flush();
                    dos.write(data, (i - 1) * size, size);
                    dos.flush();
                }

               // System.out.println(ackno);
                Thread.sleep(SLEEP);
                ackno = dis.readInt();
              // System.out.println(ackno);
                while(ackno!=i) {
                    System.out.println("xxxxxxxx");
                    if(i==totalnumber)
                    {
                        int lastsize=totalsize- (i-1)*size + 1;
                        dos.writeInt(lastsize);
                        dos.flush();
                        dos.write(data,(i - 1) * size,lastsize);
                        dos.flush();
                    }
                    else {
                        dos.writeInt(size);
                        dos.flush();
                        dos.write(data, (i - 1) * size, size);
                        dos.flush();
                    }
                    Thread.sleep(SLEEP);
                    ackno = dis.readInt();
                }

            }
            //completed = true;


        }catch(Exception e){

            logger.error("sendsinglefileerror",e);
        }
    }

    private byte[] readFile(String filename) {
//		    filename = "/" + filename;
        byte[] data = null;
        try {
            InputStream is = new FileInputStream(filename);
            File file = new File(filename);
            data = new byte[(int) file.length()];
            DataInputStream s = new DataInputStream(is);
            s.readFully(data);
            s.close();
        } catch (Exception e) {

            logger.error("readfileerror",e);
        }
        return data;
    }

   /* private void transmit(EncodedPacket packet) {
//		    filename = "/" + filename;
        byte[] data = packet.toByteArray();
        try {
            dos.writeInt(data.length);
            FileController.dijibao++;
            //System.out.println(data.length);
            //System.out.println(data.length);
            dos.flush();
            dos.write(data,0,data.length);
            dos.flush();
        } catch (Exception e1) {
            // TODO Auto-generated catch block

            logger.error("transmiterror",e1);
        }//client.getOutputStream()返回此套接字的输出流
    }*/

   /* private class Reader extends Thread{
        private InputStreamReader streamReader = new InputStreamReader(ins);
        private BufferedReader breader = new BufferedReader(streamReader);
        private int counter = 0;
        @Override
        public void run(){
            String line="";
            while(!client.isClosed()){
                if(counter < filenumber) {
                    try {
                        line = breader.readLine();

                    } catch (Exception e) {
                        // TODO Auto-generated catch block

                        logger.error("readerror",e);
                    }
                    if (line.equals("Completed")) {
                        System.out.println(line);
                        //end=System.currentTimeMillis();
                       // time=end-start;
                        completed = true;
                        counter++;
                    }
                }
            }
        }
    }*/
}
