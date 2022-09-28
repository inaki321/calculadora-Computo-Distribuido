package com.example.calculadoracomputo;

        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.Label;
        import java.io.*;
        import java.net.Socket;

public class Calculadora2Controller {
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
        String res = "";
        String res2 = "";
        number = labelDisplay.getText();
        Cliente clientObj = new Cliente();
        res = clientObj.RecieveFromUI(labelDisplay.getText());
        labelDisplay.setText(res);
    }

    static void define(String word, Writer writer, BufferedReader reader)
            throws IOException, UnsupportedEncodingException {
        writer.write(word + "\r\n");
        writer.flush();
    }


}