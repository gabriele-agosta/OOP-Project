package org.example;

import java.sql.Time;

public class Fermata {
    private Time orario;
    private String indirizzo;
    private GiornoSettimana giornoSettimana;
    private int idFermata;
    private boolean capolinea;

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

    private void setOrario(Time orario) {
        this.orario = orario;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    private void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public GiornoSettimana getGiornoSettimana() {
        return giornoSettimana;
    }

    private void setGiornoSettimana(GiornoSettimana giornoSettimana) {
        this.giornoSettimana = giornoSettimana;
    }

    public int getIdFermata() {
        return idFermata;
    }

    private void setIdFermata(int idFermata) {
        this.idFermata = idFermata;
    }

    public boolean getCapolinea() {
        return capolinea;
    }

    private void setCapolinea(boolean capolinea) {
        this.capolinea = capolinea;
    }

    public String convertCapolinea() {
        String valore = !this.capolinea ? "No" : "Si";
        return valore;
    }

}