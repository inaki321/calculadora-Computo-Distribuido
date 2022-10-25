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
    private static int nodoPuerto = 1234;

    public static ArrayList<Socket> socketsList =
            new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> oosVector =
            new ArrayList<ObjectOutputStream>();
    public static ArrayList<ObjectInputStream> oisVector =
            new ArrayList<ObjectInputStream>();

    public ArrayList<Thread> ossThreads =
            new ArrayList<Thread>();

    public static void main(String args[]) throws IOException, ClassNotFoundException{

        host = InetAddress.getLocalHost();
        for (int i = 5000; i <= 5200; i = i + 1) {
            //search for nodes between 5200 and 5000
            try {
                socket = new Socket(host.getHostName(), i);
                socketsList.add(socket);
                System.out.println("Conexion establecida con nodo en el puerto: " + Integer.toString(i));

                //read write from ObjectInputStream ObjectOutputStream objects
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oisVector.add(ois);
                oosVector.add(oos);
            }
            catch(Exception e) {
                //nothing happens
            }
        }

        //keep listens indefinitely until receives 'exit' call or program terminates

            //send operation to all nodes

            while (true) {
                for (int j =0; j <oisVector.size(); j = j + 1) {
                    System.out.println("Esperando solictud...");

                    //convert ObjectInputStream object to String
                    String message = (String) oisVector.get(j).readObject();

                    //if message recieved contains RES, a result ignore it 
                    if (!message.contains("RES")) {
                        System.out.println("Mensaje recibido del nodo(" + socketsList.get(j) + ": " + message);

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
                        System.out.println("Resultado a regresar " + lastVal);
                        oosVector.get(j).writeObject("RES:" + Double.toString(lastVal));

                        //terminate the server if client sends exit request
                        if (message.equalsIgnoreCase("exit")) break;
                    }
                }
            }

        //close resources
        //ois.close();
        //oos.close();
        //socket.close();


    }

}
