package com.example.transmission;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import com.example.controller.FileController;
import com.example.core.DecodedPacket;
import com.example.core.Decoder;
import com.example.core.EncodedPacket;
import com.example.core.IncrementalDecoder;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 服务器，全双工，支持单播和广播
 *
 * 注意是全双工，全双工，全双工
 *
 * 就是像QQ一样
 */
public class FileServer{

    private static int SLEEP=0;

    // 分配给socket连接的id，用于区分不同的socket连接
    private static int id = 0;
    // 存储socket连接，发送消息的时候从这里取出对应的socket连接
    private static int DEFAULT_SIZE = 65536;
    private static int CLOSE_FLAG = 5555;

    private HashMap<Integer,ServerThread> socketList = new HashMap<>();
    // 服务器对象，用于监听TCP端口
    private ServerSocket server;

    private String receivepath;
    Logger logger=Logger.getLogger(FileServer.class);
    /**
     * 构造函数，必须输入端口号
     */
    public FileServer(int port,String receivepath) {
        this.receivepath = receivepath;
        try {
            this.server = new ServerSocket(port);
            System.out.println("服务器启动完成 使用端口: "+port);
        } catch (Exception e) {
            logger.error("servererror",e);
        }
    }

    /**
     * 启动服务器，先让Writer对象启动等待键盘输入，然后不断等待客户端接入
     * 如果有客户端接入就开一个服务线程，并把这个线程放到Map中管理
     */
    public void start() {
        try {
            while (true) {
                Socket socket = server.accept();
                String receivedir = receivepath + socket.getInetAddress().toString()+"/";
                File dir = new File(receivedir);
                dir.mkdirs();
                System.out.println(++id + ":客户端接入:"+socket.getInetAddress() + ":" + socket.getPort());
                ServerThread thread = new ServerThread(id,socket,this);
                socketList.put(id,thread);
                thread.run();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void send(int id,String data){
        ServerThread thread = socketList.get(id);
        thread.send(data);
//        if("Completed".equals(data)){
//            thread.close();
//        }
    }

    // 服务线程，当收到一个TCP连接请求时新建一个服务线程
    private class ServerThread implements Runnable {
        private FileServer server;
        private int id;
        private String receivedir;
        private String filename;
        private Socket socket;
        private InputStream in;
        private OutputStream out;
        private PrintWriter writer;
        private DataInputStream dis;
        private FileOutputStream fos;
        private Decoder dec;
        private int filenumber;
        private int size;
        private boolean completed;

        /**
         * 构造函数
         * @param id 分配给该连接对象的id
         * @param socket 将socket连接交给该服务线程
         */
        ServerThread(int id,Socket socket,FileServer server) {
            try{
                this.server = server;
                this.receivedir = receivepath + socket.getInetAddress().toString()+"/";
                this.id = id;
                this.socket = socket;
                this.in = socket.getInputStream();
                this.out = socket.getOutputStream();
                this.writer = new PrintWriter(out);
                this.dis = new DataInputStream(socket.getInputStream());
//                this.dec = new IncrementalDecoder(DEFAULT_SIZE);
                this.completed = false;
            }catch(Exception e){
                logger.error(e);
            }
        }

        /**
         * 因为设计为全双工模式，所以读写不能阻塞，新开线程进行读操作
         */
        @Override
        public void run() {
            try {
                Thread.sleep(SLEEP);
                this.filenumber = dis.readInt();
                System.out.println(filenumber);
                Thread.sleep(SLEEP);
                this.size = dis.readInt();
                System.out.println(size);

                for(int i = 0; i<filenumber;i++) {
                    this.completed = false;
                    String flag = "file " + String.valueOf(i) + " started";
                    Thread.sleep(SLEEP);
                    int counter = dis.readInt();

                    while((size<100000000&&counter!=i+size*20)||(size>100000000&&counter!=i+10000)) {
                        //if(counter%1000==0) {
                            System.out.println(counter + "cccc");
                        //}
                        Thread.sleep(SLEEP);
                        /*byte[] a=new byte[1];
                        for(int k=0;k<counter;k++){
                            a[0]=dis.readByte();
                        }*/

                        byte[] redundancy = dis.readNBytes(counter);/////
                        Thread.sleep(SLEEP);
                        counter = dis.readInt();
                        //if(counter%1000==0) {
                            System.out.println(counter + "ccc");
                        //}
                    }
                    System.out.println(counter);
                    receiveSingleFile();
                }
                Thread.sleep(SLEEP);
                int counter = dis.readInt();
                while(counter!=CLOSE_FLAG) {
                    Thread.sleep(SLEEP);
                    /*byte[] a=new byte[1];
                    for(int k=0;k<counter;k++){
                        a[0]=dis.readByte();
                    }*/
                    byte[] redundancy = dis.readNBytes(counter);
                    Thread.sleep(SLEEP);
                    counter = dis.readInt();
                }
                System.out.println(counter);
                close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error(e);
                e.printStackTrace();
            }
        }

        /**
         * 因为同时只能有一个键盘输入，所以输入交给服务器管理而不是服务线程
         * 服务器负责选择socket连接和发送的消息内容，然后调用服务线程的write方法发送数据
         */
        public void send(String data){
            if(!socket.isClosed() && data!=null){
                writer.println(data);
                writer.flush();
            }
        }

        /**
         * 关闭所有资源
         */
        public void close(){
            try{
                if(writer!=null){
                    writer.close();
                }
                if(in!=null){
                    in.close();
                }
                if(out!=null){
                    out.close();
                }
                if(socket!=null){
                    socket.close();
                }
                socketList.remove(id);
            }catch(Exception e){
                logger.error(e);
            }
        }

        public void receiveSingleFile() {
            try {
//        		int counter = 0;
//        		dis = new DataInputStream(socket.getInputStream());
                // 文件名和长度
                Thread.sleep(SLEEP);
                int namesize=dis.readInt();
                Thread.sleep(SLEEP);
                System.out.println(namesize+"aaaa");
                /*byte[] namebyte=new byte[namesize];
                byte[] a=new byte[1];
                for(int k=0;k<namesize;k++){
                    a[0]=dis.readByte();
                    System.arraycopy(a,0,namebyte,k,1);
                }*/
                byte[] namebyte=dis.readNBytes(namesize);////
                String fileName = new String(namebyte,"UTF-8");
                //String fileName = dis.readUTF();
                System.out.println(fileName);
                this.dec = new IncrementalDecoder(size);
                System.out.println(completed);
                int baoshu=0;
                while(!completed) {
                    Thread.sleep(SLEEP);
                    int packetlength = dis.readInt();
                    //System.out.println(packetlength+"bbb");
                    Thread.sleep(SLEEP);
                    /*byte[] data =new byte[packetlength];
                    for(int k=0;k<packetlength;k++){
                        a[0]=dis.readByte();
                        System.arraycopy(a,0,data,k,1);
                    }*/
                    //packetlength-=10000;
                    byte[] data = dis.readNBytes(packetlength);
                    baoshu++;
                    EncodedPacket ep = new EncodedPacket(data);
                    DecodedPacket dp = ep.decode();

                   // System.out.println(baoshu+"...");
                    if(dp!=null) {
                        completed = dec.receive(dp);
                    }

                }
                System.out.println(receivedir+fileName);
                dec.write(new FileOutputStream(receivedir+fileName));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dec.write(bos);
                server.send(id,"Completed");
                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] ");
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }
}
