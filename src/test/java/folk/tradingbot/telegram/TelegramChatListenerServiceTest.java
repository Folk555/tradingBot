package folk.tradingbot.telegram;

import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import folk.tradingbot.telegram.models.TelegramUser;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TelegramChatListenerServiceTest {

    @Mock
    private TelegramClient telegramClient = Mockito.mock(TelegramClient.class);
    @InjectMocks
    private TelegramChatListenerService telegramChatListenerService = new TelegramChatListenerService();

    @Test
    void processMessage() {
        TdApi.UpdateNewMessage updateNewMessage = new TdApi.UpdateNewMessage();
        updateNewMessage.message = new TdApi.Message();

        TdApi.MessageSenderUser senderId = new TdApi.MessageSenderUser();
        senderId.userId = 9000;
        updateNewMessage.message.senderId = senderId;

        TdApi.MessageText messageText = new TdApi.MessageText();
        messageText.text = new TdApi.FormattedText("Hello World!", null);
        updateNewMessage.message.content = messageText;

        TelegramUpdateMessage telegramUpdateMessage = new TelegramUpdateMessage(updateNewMessage);
        Mockito.doReturn(new TelegramUser(new TdApi.User())).when(telegramClient).getMyUser();
        telegramChatListenerService.processMessage(telegramUpdateMessage);

        Mockito.verify(telegramClient).getMyUser();
    }

    @Test
    void setGetTargetChatName() {
        telegramChatListenerService = new TelegramChatListenerService();
        telegramChatListenerService.setTargetChatName(new HashSet<String>(){{add("rrr");}});
        Set<String> targetChatName = telegramChatListenerService.getTargetChatName();

        Assertions.assertTrue(targetChatName.contains("rrr"));
    }
}