package creditcardbot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyboardUtils {

    /**
     * Creates the main menu keyboard.
     *
     * @return InlineKeyboardMarkup with main menu options.
     */
    public static InlineKeyboardMarkup mainMenu() {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        InlineKeyboardButton addCardButton = InlineKeyboardButton.builder()
                .text("Add a Card")
                .callbackData("addCard")
                .build();

        InlineKeyboardButton viewCardsButton = InlineKeyboardButton.builder()
                .text("View and Adjust Cards")
                .callbackData("viewCards")
                .build();

        InlineKeyboardButton suggestCardButton = InlineKeyboardButton.builder()
                .text("Get a Suggestion")
                .callbackData("getSuggestion")
                .build();

        keyboardRows.add(Collections.singletonList(addCardButton));
        keyboardRows.add(Collections.singletonList(viewCardsButton));
        keyboardRows.add(Collections.singletonList(suggestCardButton));

        return InlineKeyboardMarkup.builder().keyboard(keyboardRows).build();
    }

    /**
     * Creates inline buttons for predefined cards that have not been added yet.
     *
     * @param userCards       List of user-added cards.
     * @param predefinedCards List of predefined card names.
     * @return InlineKeyboardMarkup with add buttons for predefined cards.
     */
    public static InlineKeyboardMarkup predefinedCardButtons(List<CreditCard> userCards, List<String> predefinedCards) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<String> addedCardNames = new ArrayList<>();

        for (CreditCard card : userCards) {
            addedCardNames.add(card.getName());
        }

        for (String card : predefinedCards) {
            if (!addedCardNames.contains(card)) {
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text("Add: " + card)
                        .callbackData("addCard_" + card)
                        .build();
                keyboardRows.add(Collections.singletonList(button));
            }
        }

        // Add a back button to return to the main menu
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("Back to Main Menu")
                .callbackData("backToMain")
                .build();
        keyboardRows.add(Collections.singletonList(backButton));

        return InlineKeyboardMarkup.builder().keyboard(keyboardRows).build();
    }

    /**
     * Creates inline buttons for adjusting user-added cards.
     *
     * @param userCards List of user-added cards.
     * @return InlineKeyboardMarkup with adjust buttons for user-added cards.
     */
    public static InlineKeyboardMarkup userCardButtons(List<CreditCard> userCards) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        for (CreditCard card : userCards) {
            InlineKeyboardButton adjustButton = InlineKeyboardButton.builder()
                    .text("Adjust: " + card.getName())
                    .callbackData("adjustCard_" + card.getName())
                    .build();
            keyboardRows.add(Collections.singletonList(adjustButton));
        }

        // Add a back button to return to the main menu
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("Back to Main Menu")
                .callbackData("backToMain")
                .build();
        keyboardRows.add(Collections.singletonList(backButton));

        return InlineKeyboardMarkup.builder().keyboard(keyboardRows).build();
    }
}
