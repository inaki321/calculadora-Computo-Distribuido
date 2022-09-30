package com.example.calculadoracomputo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    //get the localhost IP address
    public static InetAddress host;
    public static Socket socket = null;
    public static ObjectOutputStream oos = null;
    public static ObjectInputStream ois = null;

    //socket server port on which it will listen
    private static int nodo_port = 3332;

    public static void main(String args[]) throws IOException, ClassNotFoundException{

        host = InetAddress.getLocalHost();
        socket = new Socket(host.getHostName(), nodo_port);
        System.out.println("Conexion establecida con nodo en el puerto: " + Integer.toString(nodo_port));

        //read write from ObjectInputStream ObjectOutputStream objects
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){

            System.out.println("Esperando solictud...");

            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();

            System.out.println("Mensaje recibido del nodo: " + message);

            //CALCULAR
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
            System.out.println("operations array : " + operationsArray);
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
            System.out.println("RESULTADOOOO "+lastVal);
            oos.writeObject("resultado'"+Double.toString(lastVal)); // {type of message},{content}

            //CALCULAR

            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close resources
        ois.close();
        oos.close();
        socket.close();

    }
}
