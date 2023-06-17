package org.example;

import java.util.ArrayList;

public abstract class Trasporto{
    private int linea;
    private ArrayList<Fermata> fermate;

    public Trasporto(int linea, ArrayList<Fermata> fermate){
        setLinea(linea);
        setFermate(fermate);
    }

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
