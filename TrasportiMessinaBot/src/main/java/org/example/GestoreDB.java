package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class GestoreDB {
    String username, password, url;
    Connection connection;

    public GestoreDB(){
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("DATABASE_USERNAME");
        this.password = dotenv.get("DATABASE_PASSWORD");
        this.url = dotenv.get("DATABASE_URL");
        try {
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Trasporto getListaFermate(String linea, String tipo) {
        StringBuilder risultato = new StringBuilder();
        ArrayList<Fermata> fermate = new ArrayList<>();

        try {
            String query = String.format("SELECT ttf.*, f.indirizzo " +
                    "FROM trasporto_tempo_fermata ttf, fermata f, trasporto t " +
                    "WHERE ttf.id = f.id AND ttf.linea = t.linea " +
                    "AND t.tipo = ? and ttf.linea = ? ");
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tipo);
            statement.setInt(2, Integer.parseInt(linea));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Time orario = resultSet.getTime("orario");
                String indirizzo = resultSet.getString("indirizzo");
                String valoreEnum = resultSet.getString("giorno");
                GiornoSettimana giornoSettimana = GiornoSettimana.fromString(valoreEnum);
                Integer idFermata = resultSet.getInt("id");
                boolean capolinea = resultSet.getBoolean("capolinea");

                Fermata fermata = new Fermata(orario, indirizzo, giornoSettimana, idFermata, capolinea);
                fermate.add(fermata);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        Trasporto trasporto;
        trasporto = tipo.equals("bus") ? new Bus(parseInt(linea), fermate) : new Tram(parseInt(linea), fermate);

        return trasporto;
    }
}
