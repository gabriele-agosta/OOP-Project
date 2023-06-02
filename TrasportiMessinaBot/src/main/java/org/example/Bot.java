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
    private String currentView = "home";



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
        keyboardMarkup = new InlineKeyboardMarkup();

        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            message.setChatId(chatId);

            if (update.getMessage().getText().equals("/start")) {
                keyboardMarkup = createHomeInlineKeyboard();

                sendKeyboard(chatId, "Benvenuto su TrasportiMessinaBot. \n" +
                        "Per navigare le opzioni del bot, usare il menù.");
                currentView = "home";
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            message.setChatId(chatId);

            if (currentView.equals("home")) {
                if (callbackData.equals("bus")) {
                    keyboardMarkup = createIntermediateInlineKeyboard();
                    sendKeyboard(chatId, "Seleziona una delle opzioni disponibili per i bus.");
                    currentView = "bus";
                } else if (callbackData.equals("tram")) {
                    keyboardMarkup = createIntermediateInlineKeyboard();
                    sendKeyboard(chatId, "Seleziona una delle opzioni disponibili per i tram.");
                    currentView = "tram";
                }
            } else if (currentView.equals("bus")) {
                if (callbackData.equals("fermateBus")) {
                    // Handle "Seleziona tutte le fermate di una linea" option
                } else if (callbackData.equals("prossimaFermata")) {
                    // Handle "Visualizza la prossima fermata di una linea" option
                } else if (callbackData.equals("indietro")) {
                    keyboardMarkup = createHomeInlineKeyboard();
                    sendKeyboard(chatId, "Benvenuto su TrasportiMessinaBot. \n" +
                            "Per navigare le opzioni del bot, usare il menù.");
                    currentView = "home";
                }
            } else if (currentView.equals("tram")) {
                if (callbackData.equals("listaLinee")) {
                    keyboardMarkup = createListaTramInlineKeyboard();

                } else if (callbackData.equals("prossimaFermata")) {
                    //keyboardMarkup = createProssimaFermataTramInlineKeyboard();

                } else if (callbackData.equals("indietro")) {
                    keyboardMarkup = createHomeInlineKeyboard();
                    sendKeyboard(chatId, "Benvenuto su TrasportiMessinaBot. \n" +
                            "Per navigare le opzioni del bot, usare il menù.");
                    currentView = "home";
                }
            }

        }
    }

    private InlineKeyboardMarkup createHomeInlineKeyboard() {
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

        this.keyboardMarkup.setKeyboard(rowsInline);

        return this.keyboardMarkup;
    }

    private InlineKeyboardMarkup createListaTramInlineKeyboard() {
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

        this.keyboardMarkup.setKeyboard(rowsInline);

        return this.keyboardMarkup;
    }

    private InlineKeyboardMarkup createIntermediateInlineKeyboard() {
        InlineKeyboardButton listaLineeButton = new InlineKeyboardButton();
        listaLineeButton.setText("Lista fermate di una linea");
        listaLineeButton.setCallbackData("listaLinee");

        InlineKeyboardButton prossimaFermataButton = new InlineKeyboardButton();
        prossimaFermataButton.setText("Prossima fermata di una linea");
        prossimaFermataButton.setCallbackData("prossimaFermata");

        InlineKeyboardButton indietroButton = new InlineKeyboardButton();
        indietroButton.setText("Indietro");
        indietroButton.setCallbackData("indietro");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(listaLineeButton);
        row1.add(prossimaFermataButton);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(indietroButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);
        rowsInline.add(row2);

        this.keyboardMarkup.setKeyboard(rowsInline);

        return this.keyboardMarkup;
    }

    private void sendKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(this.keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}

