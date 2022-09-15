package com.example.calculadoracomputo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class slaveServerOne {
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9877;
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        //create the socket server object
        server = new ServerSocket(port);
        while (true) {
            System.out.println("Esperando la solicitud del nodo en servidor 1 ");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            System.out.println(" Mensaje recibido en el server 1: " + message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket

            //Calculando
            //character to get in each iteration
            Character numberaux;

            //aux number to use in iteration before an space
            String numberFromString = "";

            //suma a ir sumando,restando etc
            List<String> operationsArray = new ArrayList<String>();

            for (int i = 0; i <= message.length() - 1; i = i + 1) {
                numberaux = message.charAt(i);
                if (numberaux == ' ') { //remove blank spaces and get complete numbers before new number
                    operationsArray.add(numberFromString);
                    numberFromString = "";
                } else {
                    numberFromString = numberFromString + numberaux;
                }
                if (message.length() - 1 == i) {
                    operationsArray.add(numberFromString);
                }
            }
            float val = 0, res = 0, lastVal = 0;

            String opreationSymbol = "";
            for (int i = 0; i <= operationsArray.size() - 1; i = i + 1) {

                try {
                    val = Float.parseFloat(operationsArray.get(i));
                    if (opreationSymbol.equals("+")) {
                        lastVal = lastVal + val;
                    } else if (opreationSymbol.equals("-")) {
                        lastVal = lastVal - val;
                    } else if (opreationSymbol.equals("÷")) {
                        lastVal = lastVal / val;
                    } else if (opreationSymbol.equals("x")) {
                        lastVal = lastVal * val;
                    }
                    //for the first iteration just get the last value
                    if (i == 0) {
                        lastVal = val;
                    }
                } catch (Exception e) {
                    opreationSymbol = operationsArray.get(i);
                    Thread.currentThread().interrupt();
                }
            }

            //Calculando

            oos.writeObject(""+lastVal );
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if (message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Apagando servidor 1...");
        //close the ServerSocket object
        server.close();
    }

}
