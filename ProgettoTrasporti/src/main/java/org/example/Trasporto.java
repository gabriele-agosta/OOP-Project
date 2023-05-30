package org.example;

import java.util.ArrayList;

public abstract class Trasporto{
    int linea;
    ArrayList<Fermata> fermate;

    public Trasporto(int linea, ArrayList<Fermata> fermate) {
        this.linea = linea;
    }

    public Trasporto(){}

    public int getLinea() {
        return linea;
    }

    public ArrayList<Fermata> getFermate() {
        return fermate;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public void setFermate(ArrayList<Fermata> fermate) {
        this.fermate = fermate;
    }
}
