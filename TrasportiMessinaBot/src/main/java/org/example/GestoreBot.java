package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class GestoreBot implements Runnable{
    GestoreDB gestoreDB;
    Socket socket;
    ObjectInputStream input;
    PrintWriter output;

    public GestoreBot(Socket socket, GestoreDB gestoreDB){
        try{
            this.gestoreDB = gestoreDB;
            this.socket = socket;
            input = new ObjectInputStream(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        Query parametri;
        Object parametriRicevuti;
        String tipo, linea;

        try{
            parametriRicevuti = input.readObject();
            if (parametriRicevuti instanceof Query){
                parametri = (Query) parametriRicevuti;
                tipo = (String) parametri.getTipo();
                linea = (String) parametri.getLinea();
            }
        } catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }

}
