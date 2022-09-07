package com.example.calculadoracomputo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        for(int i=0; i<5;i++){
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            if(i==4)oos.writeObject("exit");
            else oos.writeObject(""+i);
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);
            //close resources
            ois.close();
            oos.close();
            Thread.sleep(100);
        }
    }
    public void RecieveFromUI(String operation) throws UnknownHostException,IOException {
        System.out.println("Recibo esto:  "+operation);
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        //establish socket connection to server
        socket = new Socket(host.getHostName(), 9876);

        //Send connection to the server
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject(operation); //data to send to the server

        //Racieve from server
        ois = new ObjectInputStream(socket.getInputStream());
        String serverMessage = "";
        try {
            serverMessage = (String) ois.readObject();
        }
        catch(Exception e) {
            System.out.println("Server return exception "+e);
        }
        System.out.println("Message from the server : " + serverMessage);

        //close client and server connection
        ois.close();
        oos.close();
        try {
            Thread.sleep(100);
        }
        catch(Exception e) {
            System.out.println("Thread exception "+e);
        }
    }
}
