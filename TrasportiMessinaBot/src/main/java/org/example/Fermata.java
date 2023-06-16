package org.example;

import java.sql.Time;

public class Fermata {
    Time orario;
    String indirizzo;
    GiornoSettimana giornoSettimana;
    int idFermata;
    boolean capolinea;

    public Fermata(Time orario, String indirizzo, GiornoSettimana giornoSettimana, int idFermata, boolean capolinea) {
        this.orario = orario;
        this.indirizzo = indirizzo;
        this.giornoSettimana = giornoSettimana;
        this.idFermata = idFermata;
        this.capolinea = capolinea;
    }

    public Time getOrario() {
        return orario;
    }

    public void setOrario(Time orario) {
        this.orario = orario;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public GiornoSettimana getGiornoSettimana() {
        return giornoSettimana;
    }

    public void setGiornoSettimana(GiornoSettimana giornoSettimana) {
        this.giornoSettimana = giornoSettimana;
    }

    public int getIdFermata() {
        return idFermata;
    }

    public void setIdFermata(int idFermata) {
        this.idFermata = idFermata;
    }

    public boolean getCapolinea() {
        return capolinea;
    }

    public void setCapolinea(boolean capolinea) {
        this.capolinea = capolinea;
    }

    public String convertCapolinea() {
        String valore = !this.capolinea ? "No" : "Si";
        return valore;
    }

}