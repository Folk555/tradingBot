package folk.tradingbot.telegram;

import folk.tradingbot.telegram.handlers.TelegramResultHandler;
import folk.tradingbot.telegram.handlers.TelegramUpdateHandler;
import folk.tradingbot.telegram.models.OrderedChat;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUser;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramClientTest {

    @Mock
    private Client client;
    @Mock
    private TelegramUpdateHandler updateHandler;
    @InjectMocks
    private TelegramClient telegramClient;

    @Test
    void send() {
        Mockito.doNothing().when(client).send(any(), any());

        telegramClient.send(any(TdApi.Function.class), any(TelegramResultHandler.class));

        Mockito.verify(client).send(any(), any());
    }

    @Test
    void getMainChatList() {
        OrderedChat orderedChatMock = mock(OrderedChat.class);
        Mockito.doReturn(1L).when(orderedChatMock).getChatId();
        Mockito.doReturn(new TreeSet<OrderedChat>(){{add(orderedChatMock); add(orderedChatMock);}})
                .when(updateHandler).getChatOrderList();
        Mockito.doReturn(new ConcurrentHashMap<Long, TdApi.Chat>(){{put(1L, new TdApi.Chat());}})
                .when(updateHandler).getChats();

        int chatOrderLimit = 1;
        List<TelegramChat> mainChatList = telegramClient.getMainChatList(chatOrderLimit);

        Assertions.assertEquals(1, mainChatList.size());
    }

    @Test
    void sendMessage() {
        Mockito.doNothing().when(client).send(any(), any());
        telegramClient.sendMessage(435L, "Hi");
        Mockito.verify(client).send(any(), any());
    }
}