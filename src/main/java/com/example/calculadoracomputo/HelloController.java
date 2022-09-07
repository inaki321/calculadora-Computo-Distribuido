package com.example.calculadoracomputo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.*;
import java.net.Socket;

public class HelloController {
    @FXML
    private Label labelDisplay;

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
        number = labelDisplay.getText();
        Cliente clientObj = new Cliente();
        clientObj.RecieveFromUI(labelDisplay.getText());
    }

    static void define(String word, Writer writer, BufferedReader reader)
            throws IOException, UnsupportedEncodingException {
        writer.write(word + "\r\n");
        writer.flush();
    }


    private void receiveFromNode() {
        String hostname = "127.0.0.1";
        Socket socket = null;
        try {
            socket = new Socket(hostname, 8000);
            socket.setSoTimeout(15000);
            InputStream in = socket.getInputStream();
            StringBuilder answer = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in, "ASCII");
            for (int c = reader.read(); c != -1; c = reader.read()) {
                answer.append((char) c);
            }
            String result = answer.toString();
            labelDisplay.setText(result);
        } catch (
                IOException ex) {
            System.err.println(ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

}