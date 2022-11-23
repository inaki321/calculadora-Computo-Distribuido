package com.computo.nodo;

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

public class NodoController {
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

        ServerSocket nodoSocket = null;
        // Lista de clients
        Random rand = new Random();
        int randomNum=0;
        try {

            boolean readyPort=false;
            while(!readyPort){
                try {
                    randomNum = rand.nextInt(5200-5000) + 5000;
                    nodoSocket = new ServerSocket(randomNum);
                    readyPort = true;
                    System.out.println("Nodo creado con puerto: "+randomNum);
                    int finalRandomNum1 = randomNum;
                    Platform.runLater(()->{
                        nodoText.appendText("Nodo creado con puerto: "+ finalRandomNum1 +"\n");
                    });
                }
                catch(Exception e) {
                    //just in case a socket port is occupied
                }
            }

            nodoSocket.setReuseAddress(true);

            // running infinite loop for getting
            // client request

            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(clientsList == null){
                    System.out.println("Esperando conexiones en el puerto: " + Integer.toString(randomNum));
                    int finalRandomNum = randomNum;
                    Platform.runLater(()->{
                        nodoText.appendText("Esperando conexiones en el puerto: " + Integer.toString(finalRandomNum)+"\n");
                    });
                }
                else {
                    System.out.println("Conexiones actuales al nodo("+randomNum+"):");
                    System.out.println(clientsList);
                    System.out.println("----------------------------------");
                }

                // socket object to receive incoming client
                // requests
                Socket client = nodoSocket.accept();
                System.out.println("chequeo "+client);

                // Displaying that new client is connected
                // to server

                System.out.println("Nueva conexion... ");
                // create a new thread object
                NodeHandler clientSock = new NodeHandler(client,nodoText);

                // This thread will handle the client
                // separately

                clientsList.add(client);
                Platform.runLater(()->{
                    nodoText.appendText("Conexiones activas: " +clientsList.size()+"\n");
                });
                System.out.println("Conexiones activas: " +clientsList.size());

                System.out.println((clientSock));
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (nodoSocket != null) {
                try {
                    nodoSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    });



    public void initialize() throws IOException, ClassNotFoundException {
        t.start();

    }



}