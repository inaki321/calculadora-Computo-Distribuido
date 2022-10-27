package com.example.calculadoracomputo;

import javafx.application.Platform;
import javafx.css.StyleableStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Calculadora1Controller {
    @FXML
    private Label labelDisplay;
    //get the localhost IP address 
    public InetAddress host;
    public Socket socket = null;
    public ObjectOutputStream oos = null;
    public ObjectInputStream ois = null;

    public ArrayList<Socket> socketsList =
            new ArrayList<Socket>();
    public ArrayList<ObjectOutputStream> oosVector =
            new ArrayList<ObjectOutputStream>();
    public ArrayList<ObjectInputStream> oisVector =
            new ArrayList<ObjectInputStream>();

    public ArrayList<Thread> ossThreads =
            new ArrayList<Thread>();

    public ArrayList<String> clientIDs =
            new ArrayList<String>();

    public void initialize() throws IOException, ClassNotFoundException {
        labelDisplay.setText("");

        //get the localhost IP address
        host = InetAddress.getLocalHost();

        // Create socket
        //nodes, we have nodes from 5200 to 5000 ports
        for (int i = 5000; i <= 5200; i = i + 1) {
            //search for nodes between 5200 and 5000
            try {
                socket = new Socket(host.getHostName(), i);
                socketsList.add(socket);
                System.out.println("Conexion establecida con nodo: " + Integer.toString(i));
                //Objects OI Stream
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                oosVector.add(oos);
                oisVector.add(ois);
                oos.writeObject("Cliente: "+socket);
                String socketID=String.valueOf(socket);
                String idSegments[] = socketID.split("localport=");
                idSegments[1] = idSegments[1].replace("]","");
                clientIDs.add(idSegments[1]);
                //Listening thread ObjectInputStream vector
                t.start();
                ossThreads.add(t);
            }
            catch(Exception e) {
                //nothing happens
            }
        }

    }

    @FXML
    public void digitEventHandler(ActionEvent event) {
        Object buttonClicked = event.getSource();
        Button digitClicked = (Button) buttonClicked;
        if (!isAcPressed(digitClicked)) {
            removeFirstZero();
            labelDisplay.setText(labelDisplay.getText() + digitClicked.getText());
        } else {
            labelDisplay.setText("0");
        }
    }

    private void removeFirstZero() {
        String currentDisplay = labelDisplay.getText();
        if (currentDisplay.startsWith("0")) {
            labelDisplay.setText(currentDisplay.substring(1));
        }
    }
    private boolean isAcPressed(Button digit) {
        if (digit.getText().equals("AC")) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    public void operationEventHandler(ActionEvent event) throws IOException {
        Object buttonClicked = event.getSource();
        Button digitClicked = (Button) buttonClicked;
        labelDisplay.setText(labelDisplay.getText() +" "+digitClicked.getText()+" ");
    }

    public void sendToClient(ActionEvent event) throws IOException {
        sendToNode(labelDisplay.getText(), event);
    }



    private void sendToNode(String number, ActionEvent event) throws IOException {
        String res = "";
        String res2 = "";
        number = labelDisplay.getText();
        event.consume();

        //operacion should not contain other symbols than */+- and numbers
        if(!number.matches(".*[a-zA-Z].*")){

            // write to socket using ObjectOutputStream
            for (int i =0; i <oosVector.size(); i = i + 1) {
                //send operation to all nodes
                System.out.println("Enviando operacion al nodo: "+socketsList.get(i) +":"+clientIDs.get(i));
                oosVector.get(i).writeObject(number+"PORT:"+clientIDs.get(i));
            }

        }
        else {
            labelDisplay.setText("Error en la expresión introducida");
        }

    }

    Thread t = new Thread(() -> {
        //Here write all actions that you want execute on background
        while(true){

            String message;
            try {
                message = (String) ois.readObject();
                String resSplit[] = message.split(":"); // {type of message},{content}

                if(resSplit[0].equals("RES")){
                    System.out.println("Resultado: "+resSplit[1]);
                    System.out.println("Numero de servidores: "+Integer.parseInt(resSplit[2]));
                    System.out.println("Puerto comparar: "+Integer.parseInt(resSplit[3]));
                }
                if(resSplit[0].equals("RES") && (Integer.parseInt(resSplit[2]))>2){
                    if(clientIDs.contains(resSplit[3])){
                        Platform.runLater(() -> {
                            labelDisplay.setText(resSplit[1]);
                        });
                    }

                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        }

    });


}