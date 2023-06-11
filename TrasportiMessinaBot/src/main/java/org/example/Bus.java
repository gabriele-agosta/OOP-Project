package org.example;

import java.util.ArrayList;

public class Bus extends Trasporto{
    public Bus(int linea, ArrayList<Fermata> fermate) {
        this.linea = linea;
        this.fermate = fermate;
    }
}
