package com.computo.servidor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServidorController {
    @FXML
    private TextArea serversystemout;

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

    public float subMicroService(float num1, float num2, String type) throws Exception {
        Platform.runLater(()->{
            serversystemout.appendText("En el microservicio \n");
            serversystemout.appendText("Recibo:"+num1+" y tambien: "+num2+"\n");
            serversystemout.appendText("Metodo a llamar:"+type+"\n");
        });
        File dir = new File("/Users/super/Desktop/7mo Semestre/computo dist/Ejemplo 1 java/calculadoraComputo/microservice/out/artifacts/microservice_jar/microservice.jar");
        Class<?> cls = new URLClassLoader(new URL[] { dir.toURI().toURL() }).loadClass("Microservicios");
        Method subMethod = cls.getMethod(type, float.class, float.class);
        Object objInstance = cls.getDeclaredConstructor().newInstance();
        float result = (float) subMethod.invoke(objInstance, num1, num2);
        return result;
    }
    Thread t = new Thread(() -> {
        //Here write all actions that you want execute on background
        while(true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(()->{
                serversystemout.appendText("Esperando solicitud... \n");
            });
            for (int j = 0; j < oisVector.size(); j = j + 1) {
                System.out.println("Esperando solictud...");

                //convert ObjectInputStream object to String
                String message = null;
                try {
                    message = (String) oisVector.get(j).readObject();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                String finalMessage = message;
                Platform.runLater(()->{
                    serversystemout.appendText("Mensaje recibido en servidor: "+ finalMessage +"\n");
                });
                System.out.println("Mensaje recibido en servidor: "+message);

                if(!(message.contains("Servidor:")) && !(message.contains("Cliente:") ) ){
                    //split message
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


                    //if message recieved contains RES, a result ignore it
                    float resValue = 0;
                    URL[] classLoaderUrls;
                    URLClassLoader urlClassLoader;
                    Class<?> clazz;
                    if (!message.contains("RES")) {
                        operationsArray.set(2, String.valueOf(operationsArray.get(2).split(":")[0]));
                        if (message.contains("+")) {
                            try {
                                resValue = subMicroService(Float.parseFloat(operationsArray.get(0)),Float.parseFloat(operationsArray.get(2)), "sumaGet");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (message.contains("-")) {
                            //Operation classes constructor's param String event, String footprint, float n1, float n2, int port
                            try {
                                resValue = subMicroService(Float.parseFloat(operationsArray.get(0)),Float.parseFloat(operationsArray.get(2)), "restaGet");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (message.contains("*")) {
                            try {
                                resValue = subMicroService(Float.parseFloat(operationsArray.get(0)),Float.parseFloat(operationsArray.get(2)), "multiGet");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (message.contains("/")) {
                            try {
                                resValue = subMicroService(Float.parseFloat(operationsArray.get(0)),Float.parseFloat(operationsArray.get(2)), "divGet");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        float finalResValue = resValue;
                        Platform.runLater(()->{
                            serversystemout.appendText("Resultado a enviar: " + finalResValue +"\n");
                            serversystemout.appendText("--------------------------------------- \n");
                        });
                        System.out.println("Resultado a enviar: " + resValue);
                        try {
                            oosVector.get(j).writeObject("RES:" + Double.toString(resValue));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        //terminate the server if client sends exit request
                        if (message.equalsIgnoreCase("exit")) break;
                    }
                }
            }
            //serversystemout.appendText("Esperando solicitud... \n");

        }

    });

    public void initialize() throws IOException, ClassNotFoundException {
        host = InetAddress.getLocalHost();
        for (int i = 5000; i <= 5200; i = i + 1) {
            //search for nodes between 5200 and 5000
            try {
                socket = new Socket(host.getHostName(), i);
                socketsList.add(socket);
                System.out.println("Conexion establecida con nodo en el puerto: " + Integer.toString(i));
                serversystemout.appendText("Conexion establecida con nodo en el puerto: " + Integer.toString(i)+"\n");

                //read write from ObjectInputStream ObjectOutputStream objects
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("Servidor: "+socket);
                oisVector.add(ois);
                oosVector.add(oos);
                t.start();
                ossThreads.add(t);
            } catch (Exception e) {
                //nothing happens
            }


        }


    }
}