package com.example.ayu.regitest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class TcpClient {
    private Socket socket;
    private DataOutputStream socketOutput;
    private DataInputStream socketInput;
    private static int BUF_SIZE = 100;
    private static final String STOP_MSG = "stop";

    private Socket client;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;

    private String ip;
    private int port;
    private ClientCallback listener=null;

    public TcpClient(String ip, int port){
        this.ip=ip;
        this.port=port;
    }

    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
                try {
                    socket.connect(socketAddress);
                    socketOutput = new DataOutputStream(socket.getOutputStream());
                    socketInput = new DataInputStream(socket.getInputStream());

                    new ReceiveThread().start();

                    if(listener!=null)
                        listener.onConnect(socket);
                } catch (IOException e) {
                    if(listener!=null)
                        listener.onConnectError(socket, e.getMessage());
                }
            }
        }).start();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    public void send(final String message){
        new Thread()
        {
            public void run()
            {
                try {
                    socketOutput.writeUTF(message);
                } catch (IOException e) {
                    if(listener!=null)
                        listener.onDisconnect(socket, e.getMessage());
                }
            }
        }.start();

    }

    private class ReceiveThread extends Thread implements Runnable{
        public void run(){
            String message;

            while (true){
                try {
                    byte[] buf = new byte[BUF_SIZE];
                    int read_Byte  = socketInput.read(buf);
                    String input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals(STOP_MSG)){
                        message = input_message;
                    }
                    else{
                        break;
                    }
                    Thread.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }


            try {
                while((message = socketInput.readLine()) != null) {

                    byte[] buf = new byte[BUF_SIZE];
                    int read_Byte  = socketInput.read(buf);

                    String input_message = new String(buf, 0, read_Byte);

                    if (!input_message.equals(STOP_MSG)){
                        message = input_message;
                    }
                    // each line must end with a \n to be received
                    Log.d("RECEIVE THREAD RUN() : ", "Progressing...");
                    if(listener!=null)
                        listener.onMessage(message);
                }
            } catch (IOException e) {
                if(listener!=null)
                    listener.onDisconnect(socket, e.getMessage());
            }

        }
    }

    public void setClientCallback(ClientCallback listener){
        this.listener=listener;
    }

    public void removeClientCallback(){
        this.listener=null;
    }

    public interface ClientCallback {
        void onMessage(String message);
        void onConnect(Socket socket);
        void onDisconnect(Socket socket, String message);
        void onConnectError(Socket socket, String message);
    }
}