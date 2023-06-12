package org.example;

import java.util.ArrayList;

public abstract class Trasporto{
    int linea;
    ArrayList<Fermata> fermate;

    public Trasporto(){}

    public int getLinea() {
        return linea;
    }

    public ArrayList<Fermata> getFermate() {
        return this.fermate;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public void setFermate(ArrayList<Fermata> fermate) {
        this.fermate = fermate;
    }
}
