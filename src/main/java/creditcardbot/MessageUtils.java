package creditcardbot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class MessageUtils {

    /**
     * Sends a plain text message to a user.
     *
     * @param chatId  The chat ID of the recipient.
     * @param text    The message text.
     * @param sender  The bot's AbsSender instance.
     */
    public static void sendText(Long chatId, String text, AbsSender sender) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message with an inline keyboard to a user.
     *
     * @param chatId   The chat ID of the recipient.
     * @param text     The message text.
     * @param keyboard The inline keyboard to attach.
     * @param sender   The bot's AbsSender instance.
     */
    public static void sendKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard, AbsSender sender) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(keyboard)
                .build();

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
