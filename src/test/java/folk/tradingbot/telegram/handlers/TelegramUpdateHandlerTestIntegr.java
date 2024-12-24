package folk.tradingbot.telegram.handlers;

import folk.tradingbot.telegram.TelegramChatListenerService;
import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TelegramUpdateHandlerTestIntegr {

    @Autowired
    TelegramUpdateHandler handler;

    @Test
    void isAuthorizated() {
        Assertions.assertTrue(handler.isAuthorizated());
    }

    @Test
    void getChats() {
        var chats = handler.getChats();
        Assertions.assertNotNull(chats);
        Optional<TelegramChat> first = chats.values().stream().map(TelegramChat::new)
                .filter(telegramChat -> telegramChat.getChatName().contains("СИГНАЛ")).findFirst();
        Assertions.assertTrue(first.isPresent());
    }

    @Test
    void getChatOrderList() {
        var chatOrderList = handler.getChatOrderList();
        Assertions.assertNotNull(chatOrderList);
    }

    @Test
    void setTelegramClient() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> handler.setTelegramClient(new TelegramClient(null)));
        Assertions.assertTrue(runtimeException.getMessage()
                .contains("В 1 TelegramUpdateHandler телеграм клиент можно задать только 1 раз"));
    }

    @Test
    void setChatListenerService() {
        TelegramChatListenerService telegramChatListenerService = new TelegramChatListenerService();
        Assertions.assertDoesNotThrow(() -> handler.setChatListenerService(telegramChatListenerService));
    }
}