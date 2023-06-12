package org.example;



import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    private InlineKeyboardMarkup keyboard;
    private ArrayList<String> stackViews;
    String linea, tipo;

    public Bot() {
        this.keyboard = new InlineKeyboardMarkup();
        this.stackViews = new ArrayList<String>();
        this.linea = null;
        this.tipo = null;
    }


    @Override
    public String getBotUsername() {
        return "trasporti_messina_bot";
    }

    @Override
    public String getBotToken() {
        Dotenv dotenv = Dotenv.load();
        String telegramBotToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        return telegramBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Thread userThread = new Thread(() -> {
            long chatId = 0;

            if (update.hasMessage() && update.getMessage().hasText()) {
                chatId = update.getMessage().getChatId();
                handleMessage(chatId, update.getMessage().getText());
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
                handleMessage(chatId, update.getCallbackQuery().getData());
            }
        });
        userThread.start();

    }

    private void handleMessage(long chatId, String messageText) {
        GestoreDB gestoreDB = new GestoreDB();
        SendMessage message = new SendMessage();
        String risultatoQuery = null;

        message.setChatId(chatId);

        risultatoQuery = handleKeyboard(messageText, gestoreDB);

        if (!messageText.equals("indietro") && !messageText.equals("/start")) {
            stackViews.add(messageText);
        }
        if (messageText.equals("/start")) {
            stackViews.add("home");
        }

        sendKeyboard(chatId, getMessageToSend(stackViews.get(stackViews.size() - 1), risultatoQuery));
    }

    private String handleKeyboard(String messageText, GestoreDB gestoreDB){
        String risultatoQuery = null;
        LocalTime currentTime = LocalTime.now();

        switch (messageText) {
            case "/start", "/home" -> keyboard = createHomeKeyboard();
            case "bus" -> {
                keyboard = createBusListKeyboard();
                tipo = messageText;
            }
            case "tram" -> {
                keyboard = createTramListKeyboard();
                tipo = messageText;
            }
            case "listaLineeBus" -> keyboard = createBusListKeyboard();
            case "listaLineeTram" -> keyboard = createTramListKeyboard();
            case "31", "s3", "28", "29" -> {
                keyboard = createQueryChoiceKeyboard();
                linea = messageText;
            }
            case "listaFermate" -> {
                risultatoQuery = getListaFermate(gestoreDB, linea, tipo);
                if (risultatoQuery.isEmpty()){
                    risultatoQuery = "Questa linea non ha ancora fermate";
                }
            }
            case "prossimaFermata" -> {
                risultatoQuery = getProssimaFermata(gestoreDB, linea, tipo, currentTime);
                if (risultatoQuery.isEmpty()){
                    risultatoQuery = "Non ci sono altre fermate per oggi";
                }
            }
            case "indietro" -> getPreviousKeyboard(risultatoQuery, gestoreDB);
        }

        return risultatoQuery;
    }

    private void handleKeyboard(String messageText, GestoreDB gestoreDB, String risultatoQuery){
        LocalTime currentTime = LocalTime.now();

        switch (messageText) {
            case "/start", "home" -> keyboard = createHomeKeyboard();
            case "bus" -> {
                keyboard = createBusListKeyboard();
                tipo = messageText;
            }
            case "tram" -> {
                keyboard = createTramListKeyboard();
                tipo = messageText;
            }
            case "listaLineeBus" -> keyboard = createBusListKeyboard();
            case "listaLineeTram" -> keyboard = createTramListKeyboard();
            case "31", "s3", "28", "29" -> {
                keyboard = createQueryChoiceKeyboard();
                linea = messageText;
            }
            case "listaFermate" -> risultatoQuery = getListaFermate(gestoreDB, linea, tipo);
            case "prossimaFermataBus", "prossimaFermataTram" -> risultatoQuery = getProssimaFermata(gestoreDB, linea, tipo, currentTime);
            case "indietro" -> getPreviousKeyboard(risultatoQuery, gestoreDB);
        }
    }

    private InlineKeyboardMarkup createHomeKeyboard() {
        InlineKeyboardButton busButton = new InlineKeyboardButton();
        busButton.setText("Bus");
        busButton.setCallbackData("bus");

        InlineKeyboardButton tramButton = new InlineKeyboardButton();
        tramButton.setText("Tram");
        tramButton.setCallbackData("tram");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(busButton);
        row.add(tramButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private InlineKeyboardMarkup createBusListKeyboard() {
        InlineKeyboardButton lineButton1 = new InlineKeyboardButton();
        lineButton1.setText("Linea 31");
        lineButton1.setCallbackData("31");

        InlineKeyboardButton lineButton2 = new InlineKeyboardButton();
        lineButton2.setText("Linea S3");
        lineButton2.setCallbackData("s3");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(lineButton1);
        row1.add(lineButton2);

        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("⬅️ Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private InlineKeyboardMarkup createTramListKeyboard() {
        InlineKeyboardButton lineButton1 = new InlineKeyboardButton();
        lineButton1.setText("Linea 28");
        lineButton1.setCallbackData("28");

        InlineKeyboardButton lineButton2 = new InlineKeyboardButton();
        lineButton2.setText("Linea 29");
        lineButton2.setCallbackData("29");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(lineButton1);
        row1.add(lineButton2);

        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("⬅️ Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private InlineKeyboardMarkup createQueryChoiceKeyboard() {
        InlineKeyboardButton listaLineeButton = new InlineKeyboardButton();
        listaLineeButton.setText("Lista fermate");
        listaLineeButton.setCallbackData("listaFermate");

        InlineKeyboardButton prossimaFermataButton = new InlineKeyboardButton();
        prossimaFermataButton.setText("Prossima fermata");
        prossimaFermataButton.setCallbackData("prossimaFermata");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(listaLineeButton);
        row1.add(prossimaFermataButton);

        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("⬅️ Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private String getListaFermate(GestoreDB gestoreDB, String linea, String tipo) {
        Trasporto trasporto = gestoreDB.getListaFermate(linea, tipo);

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️ Indietro");
        backButton.setCallbackData("indietro");

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(backButton);
        rowsInline.add(rowInline);
        keyboard.setKeyboard(rowsInline);

        StringBuilder risposta = new StringBuilder();

        for (Fermata f : trasporto.fermate) {
            risposta.append("Linea: ").append(linea).append("\n")
                    .append("Orario: ").append(f.getOrario()).append("\n")
                    .append("Giorno: ").append(f.getGiornoSettimana()).append("\n")
                    .append("Indirizzo: ").append(f.getIndirizzo()).append("\n")
                    .append("Id Fermata: ").append(f.getIdFermata()).append("\n")
                    .append("Capolinea: ").append(f.getCapolienea()).append("\n\n");
        }
        return risposta.toString();
    }

    private String getProssimaFermata(GestoreDB gestoreDB, String linea, String tipo, LocalTime currentTime) {
        Trasporto trasporto = gestoreDB.getListaFermate(linea, tipo);

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️ Indietro");
        backButton.setCallbackData("indietro");

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(backButton);
        rowsInline.add(rowInline);
        keyboard.setKeyboard(rowsInline);

        StringBuilder risposta = new StringBuilder();


        for (Fermata f : trasporto.fermate) {
            if (f.getOrario().toLocalTime().isAfter(currentTime)) {
                risposta.append("Linea: ").append(linea).append("\n")
                        .append("Orario: ").append(f.getOrario()).append("\n")
                        .append("Giorno: ").append(f.getGiornoSettimana()).append("\n")
                        .append("Indirizzo: ").append(f.getIndirizzo()).append("\n")
                        .append("Id Fermata: ").append(f.getIdFermata()).append("\n")
                        .append("Capolinea: ").append(f.getCapolienea()).append("\n\n");
                break;
            }
        }

        return risposta.toString();
    }

    private void getPreviousKeyboard(String risultatoQuery, GestoreDB gestoreDB) {
        stackViews.remove(stackViews.size() - 1);
        handleKeyboard(this.stackViews.get(stackViews.size() - 1), gestoreDB, risultatoQuery);
    }

    private String getMessageToSend(String currentView, String risultatoQuery) {
        switch (currentView) {
            case "home":
                return "Benvenuto su TrasportiMessinaBot. \n\n " +
                        "Per navigare le opzioni del bot, usare il menù.";
            case "bus":
                return "Seleziona la linea del bus";
            case "tram":
                return "Seleziona la linea del tram";
            case "listaLineeBus": case "listaLineeTram":
                return "Seleziona l'operazione da effettuare";
            case "31", "s3", "28", "29":
                return "Seleziona una linea";
            case "listaFermate", "prossimaFermata":
                return risultatoQuery;
        }
        return "";
    }

    private void sendKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(this.keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

