package com.example.calculadoracomputo;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String operacion;

    public String resul;
    public String operation;
    public Client(Socket socket, String operacion){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            this.operacion = operacion;
            this.resul = "0";
            this.operation = "0";

        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String mensaje){
        try{
            bufferedWriter.write(operacion);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                //String messageToSend = scanner.nextLine();}
                String messageToSend = mensaje;
                bufferedWriter.write( ""+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (Exception e) {
            System.out.println("hubo error mandandolo ");
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenToMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String msgFromServer;
                while (socket.isConnected()){
                    try{
                        msgFromServer = bufferedReader.readLine();
                        while (!resul.equals(msgFromServer)){
                        resul = msgFromServer;
                        System.out.println("mesaje recibido "+resul);
                        }
                    } catch (Exception e) {
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e) {
                    System.out.println("Caught:" + e);
                }
            }

        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter username: ");
        Socket socket = new Socket("localhost",1234);
        Client client = new Client(socket, "cliente");
        String operacion = scanner.nextLine();
        client.listenToMessage();
        client.sendMessage("holaa");
    }


}
