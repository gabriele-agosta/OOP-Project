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
    private InlineKeyboardMarkup keyboardMarkup;
    private String statoCorrente = "home";



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
        SendMessage message = new SendMessage();
        long chatId = 0;

        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            message.setChatId(chatId);

            if (update.getMessage().getText().equals("/start")) {
                keyboardMarkup = createHomeInlineKeyboard();
                message.setText("Benvenuto su TrasportiMessinaBot. \n" +
                        "Per navigare le opzioni del bot, usare il menù.");
                message.setReplyMarkup(keyboardMarkup);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            message.setChatId(chatId);

            if (statoCorrente.equals("home")) {
                if (callbackData.equals("bus")) {
                    keyboardMarkup = createBusInlineKeyboard();
                    message.setText("Seleziona una delle opzioni disponibili per i bus.");
                    message.setReplyMarkup(keyboardMarkup);
                    statoCorrente = "bus";
                } else if (callbackData.equals("tram")) {
                    keyboardMarkup = createTramInlineKeyboard();
                    message.setText("Seleziona una delle opzioni disponibili per i tram.");
                    message.setReplyMarkup(keyboardMarkup);
                    statoCorrente = "tram";
                }
            } else if (statoCorrente.equals("bus")) {
                if (callbackData.equals("fermateBus")) {
                    // Handle "Seleziona tutte le fermate di una linea" option
                } else if (callbackData.equals("prossimaFermata")) {
                    // Handle "Visualizza la prossima fermata di una linea" option
                } else if (callbackData.equals("indietro")) {
                    keyboardMarkup = createHomeInlineKeyboard();
                    message.setText("Benvenuto su TrasportiMessinaBot. \n" +
                            "Per navigare le opzioni del bot, usare il menù.");
                    message.setReplyMarkup(keyboardMarkup);
                    statoCorrente = "home";
                }
            }
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createHomeInlineKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

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

        keyboardMarkup.setKeyboard(rowsInline);

        return keyboardMarkup;
    }


    private InlineKeyboardMarkup createBusInlineKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton fermateBusButton = new InlineKeyboardButton();
        fermateBusButton.setText("Seleziona tutte le fermate di una linea");
        fermateBusButton.setCallbackData("fermateBus");

        InlineKeyboardButton prossimaFermataButton = new InlineKeyboardButton();
        prossimaFermataButton.setText("Visualizza la prossima fermata di una linea");
        prossimaFermataButton.setCallbackData("prossimaFermata");

        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(fermateBusButton);
        row1.add(prossimaFermataButton);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);

        keyboardMarkup.setKeyboard(rowsInline);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup createTramInlineKeyboard() {
        // Similar implementation as createBusInlineKeyboard() but for tram options
        // ...
        return keyboardMarkup;
    }
}

