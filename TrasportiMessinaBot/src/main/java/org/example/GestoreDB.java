package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
        ArrayList<Fermata> fermate = new ArrayList<>();

        try {
            String query = String.format("SELECT ttf.*, f.indirizzo " +
                    "FROM trasporto_tempo_fermata ttf, fermata f, trasporto t " +
                    "WHERE ttf.id = f.id AND ttf.linea = t.linea " +
                    "AND t.tipo = ? and ttf.linea = ? " +
                    "ORDER BY ttf.giorno, ttf.orario");
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tipo);
            statement.setInt(2, Integer.parseInt(linea));

            fermate = getResultSet(statement);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        Trasporto trasporto;
        trasporto = tipo.equals("bus") ? new Bus(parseInt(linea), fermate) : new Tram(parseInt(linea), fermate);

        return trasporto;
    }

    public Trasporto getProssimaFermata(String linea, String tipo) {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();
        ArrayList<Fermata> fermate = new ArrayList<>();
        LocalTime currentTime = LocalTime.now();


        try {
            String query = String.format("SELECT ttf.*, f.indirizzo " +
                    "FROM trasporto_tempo_fermata ttf, fermata f, trasporto t " +
                    "WHERE ttf.id = f.id AND ttf.linea = t.linea " +
                    "AND t.tipo = ? AND ttf.linea = ? " +
                    "AND ttf.giorno = ? AND ttf.orario >= ?" +
                    "ORDER BY ttf.giorno, ttf.orario");
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tipo);
            statement.setInt(2, Integer.parseInt(linea));
            statement.setString(3, String.valueOf(currentDay));
            statement.setTime(4, Time.valueOf(currentTime));

            fermate = getResultSet(statement);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        Trasporto trasporto;
        trasporto = tipo.equals("bus") ? new Bus(parseInt(linea), fermate) : new Tram(parseInt(linea), fermate);

        return trasporto;
    }

    public Trasporto getProssimoTrasporto(int idFermata) {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();
        GiornoSettimana giornoAttuale = GiornoSettimana.fromString(currentDay.toString().toLowerCase());
        ArrayList<Fermata> fermate = new ArrayList<>();
        String query = null;
        Trasporto trasporto = null;
        PreparedStatement statement;

        try {
            query = String.format("SELECT ttf.*, f.indirizzo, t.tipo " +
                    "FROM trasporto_tempo_fermata ttf, fermata f, trasporto t " +
                    "WHERE ttf.id = f.id AND ttf.linea = t.linea " +
                    "AND ttf.giorno = ? AND ttf.orario > TIME(NOW()) AND ttf.id = ? " +
                    "ORDER BY ttf.giorno, ttf.orario");
            statement = connection.prepareStatement(query);
            statement.setString(1, giornoAttuale.toString().toLowerCase());
            statement.setInt(2, idFermata);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                query = String.format("SELECT ttf.*, f.indirizzo, t.tipo " +
                        "FROM trasporto_tempo_fermata ttf, fermata f, trasporto t " +
                        "WHERE ttf.id = f.id AND ttf.linea = t.linea " +
                        "AND ttf.giorno > ? AND ttf.id = ? " +
                        "ORDER BY ttf.giorno, ttf.orario");
                statement = connection.prepareStatement(query);
                statement.setString(1, giornoAttuale.toString().toLowerCase());
                statement.setInt(2, idFermata);

                resultSet = statement.executeQuery();

                if (!resultSet.next()) {
                    query = String.format("SELECT ttf.*, f.indirizzo, t.tipo " +
                            "FROM trasporto_tempo_fermata ttf, fermata f, trasporto t " +
                            "WHERE ttf.id = f.id AND ttf.linea = t.linea " +
                            "AND ttf.giorno <= ? AND ttf.id = ? AND ttf.orario <= TIME(NOW())" +
                            "ORDER BY ttf.giorno, ttf.orario " +
                            "LIMIT 1");
                    statement = connection.prepareStatement(query);
                    statement.setString(1, giornoAttuale.toString().toLowerCase());
                    statement.setInt(2, idFermata);

                    resultSet = statement.executeQuery();
                }
            }
            if (resultSet.next()) {
                Time orario = resultSet.getTime("orario");
                String indirizzo = resultSet.getString("indirizzo");
                String valoreEnum = resultSet.getString("giorno");
                GiornoSettimana giornoSettimana = GiornoSettimana.fromString(valoreEnum);
                Integer id = resultSet.getInt("id");
                boolean capolinea = resultSet.getBoolean("capolinea");
                Integer linea = resultSet.getInt("linea");
                String tipo = resultSet.getString("tipo");

                Fermata fermata = new Fermata(orario, indirizzo, giornoSettimana, id, capolinea);
                fermate.add(fermata);
                
                trasporto = tipo.equals("bus") ? new Bus(linea, fermate) : new Tram(linea, fermate);
            }



            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return trasporto;
    }

    private ArrayList<Fermata> getResultSet(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        ArrayList<Fermata> fermate = new ArrayList<>();

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

        return fermate;
    }
}
