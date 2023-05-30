package org.progetto.oop

public class Fermata {
    LocalTime orario;
    String indirizzo;

    public Fermata(LocalTime orario, String indirizzo) {
        this.orario = orario;
        this.indirizzo = indirizzo;
    }

    public LocalTime getOrario() {
        return orario;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setOrario(LocalTime orario) {
        this.orario = orario;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}