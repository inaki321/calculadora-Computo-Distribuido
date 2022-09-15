package com.example.calculadoracomputo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

    public String RecieveFromUI(String operation) throws UnknownHostException,IOException {
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
        System.out.println("Mandando solicitud al nodo");
        oos.writeObject(operation); //data to send to the server

        //Racieve from server
        ois = new ObjectInputStream(socket.getInputStream());
        String serverMessage = "";
        try {
            serverMessage = (String) ois.readObject();
        }
        catch(Exception e) {
            System.out.println("Server regreso una excepcion "+e);
        }
        System.out.println("Mensaje del servidor : " + serverMessage);

        //close client and server connection
        ois.close();
        oos.close();
        try {
            Thread.sleep(100);
        }
        catch(Exception e) {
            System.out.println("Thread exception "+e);
        }
        return  serverMessage;
    }
}
