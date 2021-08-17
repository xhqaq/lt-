package com.example.controller;
import org.springframework.web.bind.annotation.*;
import com.example.transmission.ReceiveThread;


@RestController
@RequestMapping("file")
public class FileController {

    public void receiveThread(int port, String receivepath) throws Exception {
        ReceiveThread receiveThread = new ReceiveThread(port, receivepath);
        receiveThread.start();
    }

    public receiveInfo receivefile(int port, String receivepath) throws Exception {
        receiveThread(port, receivepath);
        return new receiveInfo(port, receivepath);
    }

    @RequestMapping(value = "receive", method = RequestMethod.POST)
    public receiveInfo receiveService(@RequestBody receiveInfo receiveInfo) throws Exception {
        System.out.println("receiveService, receiveInfo " + receiveInfo);
        receiveInfo newreceiveInfo = receivefile(receiveInfo.getPort(), receiveInfo.getReceivepath());
        return new receiveInfo(newreceiveInfo.getPort(), newreceiveInfo.getReceivepath());
    }


}
