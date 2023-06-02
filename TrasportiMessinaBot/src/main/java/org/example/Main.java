package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        GestoreDB gestoreDB = new GestoreDB();

        try {
            Connection connection = DriverManager.getConnection(gestoreDB.url, gestoreDB.username, gestoreDB.password);
            System.out.println("Connesso al database");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
            System.out.println("Connesso al bot");
        } catch (SQLException sqle){
            System.out.println("There was a problem connecting to the database");
            sqle.printStackTrace();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}