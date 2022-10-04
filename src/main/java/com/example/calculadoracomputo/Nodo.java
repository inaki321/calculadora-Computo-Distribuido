package com.example.calculadoracomputo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class Nodo {
    private static ServerSocket nodoSocket;
    private static int port = 1234;

    public static ArrayList<Socket> clientsList = new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> activeOutputStreams = new ArrayList<ObjectOutputStream>();

    public static void main(String[] args)
    {
        ServerSocket nodoSocket = null;
        // Lista de clients

        try {

            nodoSocket = new ServerSocket(port);
            nodoSocket.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                if(clientsList == null){
                    System.out.println("Esperando conexiones en el puerto: " + Integer.toString(port));
                }
                else {
                    System.out.println("Conexiones actuales al nodo: ");
                    System.out.println(clientsList);
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

                while(true){

                    //convert ObjectInputStream object to String
                    String message = (String) ois.readObject();
                    System.out.println("Mensaje recibido en el nodo : " + message);

                    // Broadcast to all active clients
                    for (int i = 0; i < activeOutputStreams.size(); i++)
                    {
                        ObjectOutputStream temp_oos = activeOutputStreams.get(i);

                        if(temp_oos != oos){
                            temp_oos.writeObject(message);
                            System.out.println("Enviando mensaje: " + message + " al cliente " + clientsList.get(i));
                            System.out.println("-----------------------------------------" );
                        }
                    }
                }

            }
            catch (IOException e) {
                System.out.println("*Conexion finalizada con el cliente: " + clientSocket.getRemoteSocketAddress());
                clientsList.remove(clientSocket);
                activeOutputStreams.remove(oos);

            } catch (ClassNotFoundException ex) {
                
//                System.Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}