package com.example.calculadoracomputo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Nodo {
    private static ServerSocket nodoSocket;
    private static int port = 1234;

    public static ArrayList<Socket> clientsList = new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> activeOutputStreams = new ArrayList<ObjectOutputStream>();

    public static ArrayList<String> serverMsgs = new ArrayList<String>();

    public static void main(String[] args)
    {
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
                }
                catch(Exception e) {
                    //just in case a socket port is occupied
                }
            }

            nodoSocket.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                if(clientsList == null){
                    System.out.println("Esperando conexiones en el puerto: " + Integer.toString(randomNum));
                }
                else {
                    System.out.println("Conexiones actuales al nodo("+randomNum+"):");
                    System.out.println(clientsList);
                    System.out.println("----------------------------------");
                }

                // socket object to receive incoming client
                // requests
                Socket client = nodoSocket.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("Nueva conexion: " + client.getRemoteSocketAddress());

                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client);

                // This thread will handle the client
                // separately

                clientsList.add(client);
                System.out.println("Conexiones activas: " +clientsList.size());

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
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectOutputStream oos;
        private final ObjectInputStream ois;

        int clientsIteration = 1;//count to see the iterations between clients
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
                System.out.println("Cuando corre esto ");
                //when you reach max count, it goes down to 0
                while(true){
                    //convert ObjectInputStream object to String
                    String message = (String) ois.readObject();
                    //System.out.println("Mensaje recibido en el nodo : " + message);
                    System.out.println("Clientes conectados "+clientsList.size());
                    System.out.println("Mensajes recibidos "+serverMsgs.size());
                    int serverCount = 0;
                    serverMsgs.add(message);
                    if(serverMsgs.size() == clientsList.size()){
                        System.out.println("Di una vuelta a los clientes "+serverMsgs);
                        for(int k =0; k<=serverMsgs.size()-1;k=k+1 ){
                            System.out.println("conexiones "+serverMsgs.get(k));
                            if(serverMsgs.get(k).contains("RES")){
                                serverCount = serverCount + 1;
                            }
                        }
                    }
                    // Broadcast to all active clients
                    for (int i = 0; i < activeOutputStreams.size(); i++)
                    {
                        ObjectOutputStream temp_oos = activeOutputStreams.get(i);
                        //check for 3 messages from server


                        if(temp_oos != oos){
                            temp_oos.writeObject(message+":"+serverCount);
                            System.out.println("Enviando mensaje: " + message + " a las conexiones existentes ( "+clientsList.size()+") "+  clientsList.get(i));
                            System.out.println("////////////////////////" );
                        }
                    }
                    if(serverMsgs.size() == clientsList.size()){
                        serverMsgs.clear();
                    }
                    System.out.println("-----------------------------------------" );
                    //serverMsgs.clear();
                   //System.out.println("ARREGLO AL FINAL "+serverMsgs);
                }

            }
            catch (IOException e) {
                System.out.println("*Conexion finalizada con: " + clientSocket.getRemoteSocketAddress());
                clientsList.remove(clientSocket);
                activeOutputStreams.remove(oos);

            } catch (ClassNotFoundException ex) {
                
//                System.Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
