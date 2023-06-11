package org.example;



import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
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

        message.setChatId(chatId);
        // bus e tram portano alla lista dei bus e dei tram.
        // listaLineeBus e listaLineeTram listano i bus e i tram disponibili
        // Devo aggiungere un message text per ogni linea ora
        switch (messageText) {
            case "/start" -> keyboard = createHomeKeyboard();
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
            case "listaFermate" -> keyboard = getListaFermate(gestoreDB, linea, tipo);
            case "prossimaFermataBus", "prossimaFermataTram" -> keyboard = getProssimaFermata();
            case "indietro" -> keyboard = getPreviousKeyboard();
        }

        if (!messageText.equals("indietro")) {
            stackViews.add(messageText);
        }
        if (messageText.equals("/start")) {
            stackViews.add("home");
        }
        sendKeyboard(chatId, getMessageToSend(stackViews.get(stackViews.size() - 1)));
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
        indietroButton.setText("Indietro");
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
        indietroButton.setText("Indietro");
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
        indietroButton.setText("Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);

        this.keyboard.setKeyboard(rowsInline);

        return this.keyboard;
    }

    private InlineKeyboardMarkup getListaFermate(GestoreDB gestoreDB, String linea, String tipo) {
        gestoreDB.getListaFermate(linea, tipo);

        return null;
    }

    private InlineKeyboardMarkup getProssimaFermata() {
        return null;
    }

    private InlineKeyboardMarkup getPreviousKeyboard() {
        return null;
    }

    private String getMessageToSend(String currentView) {
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
            case "listaFermate":
                return "Ecco tutte le fermate della linea da te selezionata";
            case "listaProssimaFermata":
                return "Ecco la prossima fermata della linea da te selezionata";
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

