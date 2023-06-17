package org.example;



import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Bot extends TelegramLongPollingBot {
    private InlineKeyboardMarkup keyboard;
    private ConcurrentHashMap<Long, Stack<String>> userStackViewsMap;
    private String linea, tipo;
    private int fermata;

    public Bot() {
        this.keyboard = new InlineKeyboardMarkup();
        this.userStackViewsMap = new ConcurrentHashMap<>();
        this.linea = null;
        this.tipo = null;
        this.fermata = 0;
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

        Stack<String> stackViews = userStackViewsMap.get(chatId);
        if (stackViews == null) {
            stackViews = new Stack<>();
            stackViews.add("home");
            userStackViewsMap.put(chatId, stackViews);
        }

        if (!messageText.equals("indietro") && !messageText.equals("/start")) {
            stackViews.add(messageText);
        }

        risultatoQuery = handleKeyboard(messageText, gestoreDB, userStackViewsMap.get(chatId));

        sendKeyboard(chatId, getMessageToSend(userStackViewsMap.get(chatId).peek(), risultatoQuery));
    }

    private String handleKeyboard(String messageText, GestoreDB gestoreDB, Stack<String> stackViews){
        String risultatoQuery = null;
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
            case "31", "30", "28", "29" -> {
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
            case "prossimoTrasporto" -> {
                keyboard = createAddressesListKeyboard();
            }
            case "1", "2", "3" -> {
                fermata = Integer.parseInt(messageText);
                risultatoQuery = getProssimoTrasporto(gestoreDB, fermata);
                if (risultatoQuery.isEmpty()){
                    risultatoQuery = "Questa fermata non è attualmente coperta";
                }
            }
            case "indietro" -> getPreviousKeyboard(risultatoQuery, gestoreDB, stackViews);
        }

        return risultatoQuery;
    }

    private void handleKeyboard(String messageText, GestoreDB gestoreDB, String risultatoQuery, Stack<String> stackViews){
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
            case "31", "30", "28", "29" -> {
                keyboard = createQueryChoiceKeyboard();
                linea = messageText;
            }
            case "listaFermate" -> risultatoQuery = getListaFermate(gestoreDB, linea, tipo);
            case "prossimaFermata" -> risultatoQuery = getProssimaFermata(gestoreDB, linea, tipo, currentTime);
            case "prossimoTrasporto" -> {
                keyboard = createAddressesListKeyboard();
            }
            case "indietro" -> getPreviousKeyboard(risultatoQuery, gestoreDB, stackViews);
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

        InlineKeyboardButton prossimoTrasportoButton = new InlineKeyboardButton();
        prossimoTrasportoButton.setText("Prossimo trasporto di una fermata");
        prossimoTrasportoButton.setCallbackData("prossimoTrasporto");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(prossimoTrasportoButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row);
        rowsInline.add(row2);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private InlineKeyboardMarkup createBusListKeyboard() {
        InlineKeyboardButton lineButton1 = new InlineKeyboardButton();
        lineButton1.setText("Linea 31");
        lineButton1.setCallbackData("31");

        InlineKeyboardButton lineButton2 = new InlineKeyboardButton();
        lineButton2.setText("Linea 30");
        lineButton2.setCallbackData("30");

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

    private InlineKeyboardMarkup createAddressesListKeyboard() {
        InlineKeyboardButton lineButton1 = new InlineKeyboardButton();
        lineButton1.setText("Viale Ferdinando Stagno d'Alcontres");
        lineButton1.setCallbackData("1");

        InlineKeyboardButton lineButton2 = new InlineKeyboardButton();
        lineButton2.setText("Via Garibaldi");
        lineButton2.setCallbackData("2");

        InlineKeyboardButton lineButton3 = new InlineKeyboardButton();
        lineButton3.setText("Viale Giostra");
        lineButton3.setCallbackData("3");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(lineButton1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(lineButton2);
        row2.add(lineButton3);


        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("⬅️ Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private InlineKeyboardMarkup createQueryChoiceKeyboard() {
        InlineKeyboardButton listaLineeButton = new InlineKeyboardButton();
        listaLineeButton.setText("Lista fermate");
        listaLineeButton.setCallbackData("listaFermate");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(listaLineeButton);

        InlineKeyboardButton prossimaFermataButton = new InlineKeyboardButton();
        prossimaFermataButton.setText("Prossima fermata di oggi");
        prossimaFermataButton.setCallbackData("prossimaFermata");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(prossimaFermataButton);

        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("⬅️ Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);

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

        for (Fermata f : trasporto.getFermate()) {
            buildResult(linea, risposta, f);
        }
        return risposta.toString();
    }

    private String getProssimaFermata(GestoreDB gestoreDB, String linea, String tipo, LocalTime currentTime) {
        Trasporto trasporto = gestoreDB.getProssimaFermata(linea, tipo);

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️ Indietro");
        backButton.setCallbackData("indietro");

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(backButton);
        rowsInline.add(rowInline);
        keyboard.setKeyboard(rowsInline);

        StringBuilder risposta = new StringBuilder();


        for (Fermata f : trasporto.getFermate()) {
            if (f.getOrario().toLocalTime().isAfter(currentTime)) {
                buildResult(linea, risposta, f);
                break;
            }
        }

        return risposta.toString();
    }

    private void buildResult(String linea, StringBuilder risposta, Fermata f) {
        risposta.append("Linea: ").append(linea).append("\n")
                .append("Orario: ").append(f.getOrario()).append("\n")
                .append("Giorno: ").append(f.getGiornoSettimana()).append("\n")
                .append("Indirizzo: ").append(f.getIndirizzo()).append("\n")
                .append("Id Fermata: ").append(f.getIdFermata()).append("\n")
                .append("Capolinea: ").append(f.convertCapolinea()).append("\n\n");
    }

    private String getProssimoTrasporto(GestoreDB gestoreDB, int idFermata){
        Trasporto trasporto = gestoreDB.getProssimoTrasporto(idFermata);
        StringBuilder risposta = new StringBuilder();

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️ Indietro");
        backButton.setCallbackData("indietro");

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(backButton);
        rowsInline.add(rowInline);
        keyboard.setKeyboard(rowsInline);

        if (trasporto != null) {
            ArrayList<Fermata> fermate = trasporto.getFermate();
            Fermata f = fermate.get(0);

            risposta.append("Linea: ").append(trasporto.getLinea()).append("\n")
                    .append("Orario: ").append(f.getOrario()).append("\n")
                    .append("Giorno: ").append(f.getGiornoSettimana()).append("\n")
                    .append("Indirizzo: ").append(f.getIndirizzo()).append("\n")
                    .append("Id Fermata: ").append(f.getIdFermata()).append("\n")
                    .append("Capolinea: ").append(f.convertCapolinea()).append("\n\n");
        }

        return risposta.toString();
    }

    private void getPreviousKeyboard(String risultatoQuery, GestoreDB gestoreDB, Stack<String> stackViews) {
        stackViews.pop();

        String previousView = stackViews.isEmpty() ? "home" : stackViews.peek();

        handleKeyboard(previousView, gestoreDB, risultatoQuery, stackViews);
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
            case "31", "30", "28", "29":
                return "Seleziona una linea";
            case "prossimoTrasporto":
                return "Seleziona la fermata";
            case "listaFermate", "prossimaFermata", "1", "2", "3":
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

