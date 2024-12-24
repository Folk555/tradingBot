package folk.tradingbot.telegram.models;

import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramChatTest {

    private TelegramChat telegramChat;

    {
        TdApi.Chat chat = new TdApi.Chat();
        chat.id = 333;
        chat.title = "Hello World";
        telegramChat = new TelegramChat(chat);
    }

    @Test
    void getId() {
        assertEquals(333, telegramChat.getId());
    }

    @Test
    void getChatName() {
        assertEquals("Hello World", telegramChat.getChatName());
    }
}