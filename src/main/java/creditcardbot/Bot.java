package creditcardbot;

import io.github.cdimascio.dotenv.Dotenv;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    // Bot credentials from environment variables
    private final String botToken;
    private final String botUsername;

    // In-memory storage for user-specific credit card data
    private final Map<Long, List<CreditCard>> userCreditCards = new HashMap<>();
    private final Map<Long, String> awaitingInput = new HashMap<>(); // Track manual input requests


    // Predefined list of popular credit cards
    private final List<String> predefinedCreditCards = Arrays.asList(
            "Chase Sapphire Preferred",
            "Amazon Prime Rewards",
            "Bank of America Cash Rewards",
            "Discover It Cashback",
            "Apple Card",
            "Citi Double Cash",
            "Capital One Quicksilver",
            "American Express Platinum",
            "American Express Blue Cash Everyday",
            "Wells Fargo Active Cash",
            "US Bank Altitude Connect",
            "Barclays Arrival Plus"
    );

    // Constructor to initialize the bot
    public Bot() {

//        Dotenv dotenv = Dotenv.load();
//        this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
//        this.botUsername = dotenv.get("TELEGRAM_BOT_USERNAME");

        this.botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        this.botUsername = System.getenv("TELEGRAM_BOT_USERNAME");


        if (this.botToken == null || this.botUsername == null) {
            throw new IllegalStateException("Environment variables TELEGRAM_BOT_TOKEN and TELEGRAM_BOT_USERNAME must be set.");
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage().getChatId(), update.getMessage().getText());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery().getId(), update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom().getId());
        }
    }

    private void handleMessage(Long chatId, String userMessage) {
        if (awaitingInput.containsKey(chatId)) {
            handleManualInput(chatId, userMessage.trim());
        } else {
            switch (userMessage) {
                case "/start", "/menu" -> showMainMenu(chatId);
                default -> MessageUtils.sendText(chatId, "Unknown command. Use /menu to return to the main menu.", this);
            }
        }
    }

    private void handleCallbackQuery(String queryId, String data, Long userId) {
        try {
            execute(AnswerCallbackQuery.builder().callbackQueryId(queryId).build());
            if (data.equals("addCard")) {
                showPredefinedCardOptions(userId);
            } else if (data.equals("viewCards")) {
                showUserAddedCards(userId);
            } else if (data.equals("getSuggestion")) {
                suggestCard(userId);
            } else if (data.startsWith("addCard_")) {
                String cardName = data.replace("addCard_", "");
                addCardForUser(userId, cardName);
                MessageUtils.sendText(userId, cardName + " has been added!", this);
                showMainMenu(userId);
            } else if (data.startsWith("adjustCard_")) {
                String cardName = data.replace("adjustCard_", "");
                requestManualInput(userId, "dueDate_" + cardName, "Please type the new due date for " + cardName + " (e.g., 15).");
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showMainMenu(Long chatId) {
        MessageUtils.sendKeyboard(chatId, "Main Menu:\n\nChoose an option below:", KeyboardUtils.mainMenu(), this);
    }

    private void showPredefinedCardOptions(Long chatId) {
        List<CreditCard> userCards = userCreditCards.getOrDefault(chatId, Collections.emptyList());
        MessageUtils.sendKeyboard(chatId, "Select a card to add:", KeyboardUtils.predefinedCardButtons(userCards, predefinedCreditCards), this);
    }

    private void showUserAddedCards(Long chatId) {
        List<CreditCard> userCards = userCreditCards.getOrDefault(chatId, Collections.emptyList());
        if (userCards.isEmpty()) {
            MessageUtils.sendText(chatId, "No cards added yet. Use 'Add a Card' to start.", this);
        } else {
            StringBuilder summary = new StringBuilder("Your Cards:\n\n");
            for (CreditCard card : userCards) {
                summary.append(card.getName())
                        .append(" - Due Date: ")
                        .append(card.getDueDate())
                        .append("\n");
            }
            MessageUtils.sendKeyboard(chatId, summary.toString(), KeyboardUtils.userCardButtons(userCards), this);
        }
    }

    private void suggestCard(Long chatId) {
        List<CreditCard> cards = userCreditCards.getOrDefault(chatId, Collections.emptyList());
        if (cards.isEmpty()) {
            MessageUtils.sendText(chatId, "You have no cards configured. Use 'Add a Card' to add cards.", this);
            return;
        }

        LocalDate today = LocalDate.now();
        int todayDate = today.getDayOfMonth();

        // Find the best card
        Optional<CreditCard> bestCard = cards.stream()
                .filter(card -> todayDate > card.getClosingDate()) // Only consider cards with closed statements
                .max(Comparator.comparingInt(CreditCard::getDueDate)); // Prefer cards with the latest due dates

        if (bestCard.isPresent()) {
            CreditCard card = bestCard.get();

            // Calculate the payment due month
            LocalDate paymentDueDate = calculateDueDate(todayDate, card.getDueDate(), today.getMonthValue(), today.getYear());
            String paymentMonth = paymentDueDate.getMonth().name();
            int paymentDay = paymentDueDate.getDayOfMonth();

            // Reasoning for card selection
            String reasoning = "This card is recommended because its statement has already closed, and it has the latest payment due date.";

            // Send the detailed message
            String message = String.format(
                    "Recommended Card: %s\nPayment Due: %s %d\nReason: %s",
                    card.getName(),
                    paymentMonth,
                    paymentDay,
                    reasoning
            );

            MessageUtils.sendText(chatId, message, this);
        } else {
            MessageUtils.sendText(chatId, "No suitable card found. Ensure your due dates are configured.", this);
        }
    }

    private LocalDate calculateDueDate(int todayDate, int dueDate, int currentMonth, int currentYear) {
        return dueDate < todayDate
                ? LocalDate.of(currentYear, currentMonth, 1).plusMonths(1).withDayOfMonth(dueDate)
                : LocalDate.of(currentYear, currentMonth, 1).withDayOfMonth(dueDate);
    }

    private void handleManualInput(Long chatId, String input) {
        String action = awaitingInput.remove(chatId);
        String[] parts = action.split("_");
        String type = parts[0];
        String cardName = parts[1];

        try {
            int date = Integer.parseInt(input);
            if (type.equals("dueDate")) {
                adjustCardDueDate(chatId, cardName, date);
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendText(chatId, "Invalid input. Please type a valid number.", this);
        }
    }

    private void requestManualInput(Long chatId, String action, String prompt) {
        awaitingInput.put(chatId, action);
        MessageUtils.sendText(chatId, prompt, this);
    }

    private void adjustCardDueDate(Long chatId, String cardName, int dueDate) {
        List<CreditCard> userCards = userCreditCards.getOrDefault(chatId, Collections.emptyList());
        for (CreditCard card : userCards) {
            if (card.getName().equals(cardName)) {
                card.setDueDate(dueDate);
                card.setClosingDate(Math.max(dueDate - 2, 1));
                MessageUtils.sendText(chatId, "Updated " + cardName + " to Due Date: " + dueDate, this);
                return;
            }
        }
        MessageUtils.sendText(chatId, "Card not found: " + cardName, this);
    }
    private void addCardForUser(Long userId, String cardName) {
        // Ensure user has a card list
        userCreditCards.putIfAbsent(userId, new ArrayList<>());

        // Check for duplicates
        List<CreditCard> userCards = userCreditCards.get(userId);
        for (CreditCard card : userCards) {
            if (card.getName().equalsIgnoreCase(cardName)) {
                MessageUtils.sendText(userId, cardName + " is already in your list.", this);
                return;
            }
        }

        // Add the new card
        CreditCard newCard = new CreditCard(cardName);
        userCards.add(newCard);
        MessageUtils.sendText(userId, cardName + " has been successfully added to your cards.", this);

        // Optionally show the card summary
        showUserAddedCards(userId);
    }

}