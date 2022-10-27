package com.example.calculadoracomputo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Nodo1Controller {
    @FXML
    private TextArea nodoText;

    private static int port = 1234;

    public static ArrayList<Socket> clientsList = new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> activeOutputStreams = new ArrayList<ObjectOutputStream>();

    public static ArrayList<String> serverMsgs = new ArrayList<String>();
    public static ArrayList<String> serversID = new ArrayList<String>();
    public static ArrayList<String> clientsID = new ArrayList<String>();

    public static String lastPort = new String();



    Thread t = new Thread(() -> {
        //Here write all actions that you want execute on background
        while(true){

        }

    });


    public void initialize() throws IOException, ClassNotFoundException {
        ServerSocket nodoSocket = null;
        // Lista de clients
        Random rand = new Random();
        int randomNum = 0;
        try {

            boolean readyPort = false;
            while (!readyPort) {
                try {
                    randomNum = rand.nextInt(5200 - 5000) + 5000;
                    nodoSocket = new ServerSocket(randomNum);
                    readyPort = true;
                    int finalRandomNum = randomNum;
                    Platform.runLater(() -> {
                        nodoText.appendText("Nodo creado con puerto: " + finalRandomNum +"\n");
                    });
                    System.out.println("Nodo creado con puerto: " + randomNum);
                } catch (Exception e) {
                    //just in case a socket port is occupied
                }
            }

            nodoSocket.setReuseAddress(true);



            } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }


    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectOutputStream oos;
        private final ObjectInputStream ois;

        int clientsIteration = 0;//count to see the iterations between clients
        // Constructor
        public ClientHandler(Socket socket) throws IOException
        {
            this.clientSocket = socket;
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            activeOutputStreams.add(oos); // add oos a la lista
        }

        public void run()
        {
            try {
                //when you reach max count, it goes down to 0
                while(true){
                    //convert ObjectInputStream object to String
                    String message = (String) ois.readObject();
                    //System.out.println("Mensaje recibido en el nodo : " + message);
                    System.out.println("No. de conexiones activas "+clientsList.size());
                    //serverMsgs.add(message);

                    System.out.println("MENSAJE RECIBIDO EN EL NODO: "+message);
                    //add servers when connect
                    //just one message when it starts
                    if(message.contains("Servidor")){
                        serversID.add(message);
                    }
                    if(message.contains("Cliente")){
                        clientsID.add(message);
                    }
                    if(message.contains("PORT")){
                        String portRecieved[] = message.split("PORT:");
                        lastPort = portRecieved[1];
                        message = portRecieved[0];
                    }
                    System.out.println("Calculadora que envio el dato "+lastPort);
                    System.out.println("Servidores: "+serversID.size() +" // "+serversID);
                    System.out.println("Clientes: "+clientsID.size() +" // "+clientsID);


                    for (int i = 0; i < activeOutputStreams.size(); i++)
                    {
                        ObjectOutputStream temp_oos = activeOutputStreams.get(i);
                        //check for 3 messages from server

                        if(temp_oos != oos){
                            temp_oos.writeObject(message+":"+serversID.size()+":"+lastPort);
                            System.out.println("Enviando mensaje: " + message + " a las conexiones existentes ( "+clientsList.size()+") "+  clientsList.get(i));
                            System.out.println("");
                        }
                    }
                    System.out.println("-----------------------------------------" );
                    //serverMsgs.clear();
                }

            }
            catch (IOException e) {
                System.out.println("*Conexion finalizada con: " + clientSocket.getRemoteSocketAddress());
                String socketRemoved=String.valueOf(clientSocket.getRemoteSocketAddress());
                String segments[] = socketRemoved.split(":");
                for(int c=0 ;c< serversID.size();c=c+1){
                    String serverToRemove = serversID.get(c).replace("Servidor: ","");
                    String segmentsLoop[] = serverToRemove.split("localport=");
                    segmentsLoop[1] = segmentsLoop[1].replace("]","");
                    if(segmentsLoop[1].equals(segments[1])){
                        System.out.println("Quitar este "+serversID.remove(c));
                    }
                }
                for(int c=0 ;c< clientsID.size();c=c+1){
                    String serverToRemove = clientsID.get(c).replace("Servidor: ","");
                    String segmentsLoop[] = serverToRemove.split("localport=");
                    segmentsLoop[1] = segmentsLoop[1].replace("]","");

                    if(segmentsLoop[1].equals(segments[1])){
                        System.out.println("Quitar este "+clientsID.remove(c));
                    }
                }
                System.out.println("Servers after remove: "+serversID);
                System.out.println("Clients after remove: "+clientsID);
                clientsList.remove(clientSocket);
                activeOutputStreams.remove(oos);
            } catch (ClassNotFoundException ex) {

//                System.Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
