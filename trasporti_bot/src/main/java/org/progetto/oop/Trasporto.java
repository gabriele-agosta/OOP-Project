package org.progetto.oop

public abstract class Trasporto implements Serializable{
    int linea;
    ArrayList<Fermata> fermate;

    public Trasporto(int linea, ArrayList<Fermata> fermate) {
        this.linea = linea;
        this.fermate =
    }

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