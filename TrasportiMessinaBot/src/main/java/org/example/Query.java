package org.example;

import java.io.Serializable;

public class Query implements Serializable {
    String tipo, linea;

    public Query(String tipo, String linea) {
        this.tipo = tipo;
        this.linea = linea;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLinea() {
        return linea;
    }
}
