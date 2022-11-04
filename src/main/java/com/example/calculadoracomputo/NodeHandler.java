package com.example.calculadoracomputo;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NodeHandler implements Runnable {
    private final Socket clientSocket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    public static ArrayList<Socket> clientsList = new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> activeOutputStreams = new ArrayList<ObjectOutputStream>();

    public static ArrayList<String> serverMsgs = new ArrayList<String>();
    public static ArrayList<String> serversID = new ArrayList<String>();
    public static ArrayList<String> clientsID = new ArrayList<String>();

    public static String lastPort = new String();

    private TextArea textUI = null;

    int clientsIteration = 0;//count to see the iterations between clients
    // Constructor
    public NodeHandler(Socket socket,TextArea text ) throws IOException
    {
        this.clientSocket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
        activeOutputStreams.add(oos); // add oos a la lista
        this.textUI = text;
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
                String finalMessage = message;
                Platform.runLater(()->{
                    textUI.appendText("MENSAJE RECIBIDO EN EL NODO: "+ finalMessage+"\n");
                });
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
                Platform.runLater(()->{
                    textUI.appendText("Calculadora que envio el dato "+lastPort+"\n");
                    textUI.appendText("Servidores: "+serversID.size() +" // "+serversID+"\n");
                    textUI.appendText("Clientes: "+clientsID.size() +" // "+clientsID+"\n");
                });
                System.out.println("Calculadora que envio el dato "+lastPort);
                System.out.println("Servidores: "+serversID.size() +" // "+serversID);
                System.out.println("Clientes: "+clientsID.size() +" // "+clientsID);


                for (int i = 0; i < activeOutputStreams.size(); i++)
                {
                    ObjectOutputStream temp_oos = activeOutputStreams.get(i);
                    //check for 3 messages from server

                    if(temp_oos != oos){
                        temp_oos.writeObject(message+":"+serversID.size()+":"+lastPort);
                        String finalMessage1 = message;
                        Platform.runLater(()->{
                            textUI.appendText(finalMessage1 +":"+serversID.size()+":"+lastPort+"\n");
                        });
                        //System.out.println("Enviando mensaje: " + message + " a las conexiones existentes ( "+clientsList.size()+") "+  clientsList.get(i));
                        System.out.println("");
                    }
                }
                Platform.runLater(()->{
                    textUI.appendText("-----------------------------------------" +"\n");
                });
                System.out.println("-----------------------------------------" );
                //serverMsgs.clear();
            }

        }
        catch (IOException e) {
            System.out.println("*Removed conection: " + clientSocket.getRemoteSocketAddress());
            Platform.runLater(()->{
                textUI.appendText("*Removed conection: " + clientSocket.getRemoteSocketAddress()+"\n");
            });
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
            Platform.runLater(()->{
                textUI.appendText("Servers after remove: "+serversID+"\n");
                textUI.appendText("Clients after remove: "+clientsID+"\n");
            });
            System.out.println("Servers after remove: "+serversID);
            System.out.println("Clients after remove: "+clientsID);
            clientsList.remove(clientSocket);
            activeOutputStreams.remove(oos);
        } catch (ClassNotFoundException ex) {

//                System.Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}