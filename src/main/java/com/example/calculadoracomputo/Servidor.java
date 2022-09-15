package com.example.calculadoracomputo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            System.out.println("Esperando la solicitud del cliente ");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();

            // send message to the server OMKFO ----------------------------
            String serverMessage = "";
            List<Integer> serversList = new ArrayList<Integer>();
            serversList.add(9877);
            serversList.add(9878);
            for (int i = 0; i < 2; i++) {
            InetAddress host = InetAddress.getLocalHost();
            Socket socketS = null;
            ObjectOutputStream oosS = null;
            ObjectInputStream oisS = null;
            //establish socket connection to server
            //socketS = new Socket(host.getHostName(), 9877);
                socketS = new Socket(host.getHostName(), serversList.get(i));

            //Send connection to the server
            oosS = new ObjectOutputStream(socketS.getOutputStream());
            System.out.println("Mandando solicitud al servidor: "+(i+1));
            oosS.writeObject(message); //data to send to the server

            //Recieve from server
            oisS = new ObjectInputStream(socketS.getInputStream());

            try {
                serverMessage = (String) oisS.readObject();
            }
            catch(Exception e) {
                System.out.println("Server regreso una excepcion "+e);
            }
            System.out.println("Servidor: "+ (i+1 )+" Mensaje: "+ serverMessage);
            }
            //send message to other servers OMKFO ------------------------------

            System.out.println(" Mensaje recibido en el nodo: " + message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket

            oos.writeObject(""+serverMessage );
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if (message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Apagando servidor...");
        //close the ServerSocket object
        server.close();
    }
}
