package org.example;

import io.github.cdimascio.dotenv.Dotenv;

public class GestoreDB {
    String username, password, url;

    public GestoreDB(){
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("DATABASE_USERNAME");
        this.password = dotenv.get("DATABASE_PASSWORD");
        this.url = dotenv.get("DATABASE_URL")

    }
}
