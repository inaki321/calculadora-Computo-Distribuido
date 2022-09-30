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

public class Calculadora1Controller {
    @FXML
    private Label labelDisplay;
    //get the localhost IP address 
    public InetAddress host;
    public Socket socket = null;
    public ObjectOutputStream oos = null;
    public ObjectInputStream ois = null;

    public int nodePort = 3332;

    public void initialize() throws IOException, ClassNotFoundException {
        labelDisplay.setText("");

        //get the localhost IP address
        host = InetAddress.getLocalHost();

        // Create socket
        socket = new Socket(host.getHostName(), nodePort);
        System.out.println("[cliente] Conexion establecida con nodo: " + Integer.toString(nodePort));

        //Objects OI Stream
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());

        //Listening thread ObjectInputStream
        t.start();
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
            System.out.println("Enviando datos al nodo: " + "operacion,"+number);
            oos.writeObject(number);

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
                System.out.println("Respuesta recibida en el cliente: " + message);

                String parts[] = message.split("'"); // {type of message},{content}

                if(parts[0].equals("resultado")){
                    Platform.runLater(() -> {
                        labelDisplay.setText(parts[1]);
                    });
                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        }

    });


}