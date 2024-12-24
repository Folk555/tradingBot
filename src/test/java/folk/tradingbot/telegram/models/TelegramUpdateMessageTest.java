package folk.tradingbot.telegram.models;

import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramUpdateMessageTest {

    private TelegramUpdateMessage telegramUpdateMessage;

    {
        TdApi.UpdateNewMessage updateNewMessage = new TdApi.UpdateNewMessage();
        updateNewMessage.message = new TdApi.Message();

        TdApi.MessageSenderUser senderId = new TdApi.MessageSenderUser();
        senderId.userId = 9000;
        updateNewMessage.message.senderId = senderId;

        TdApi.MessageText messageText = new TdApi.MessageText();
        messageText.text = new TdApi.FormattedText("Hello World!", null);
        updateNewMessage.message.content = messageText;

        telegramUpdateMessage = new TelegramUpdateMessage(updateNewMessage);
    }

    @Test
    void getMessageSenderId() {
        assertEquals(9000, telegramUpdateMessage.getMessageSenderId());
    }

    @Test
    void getMessageContent() {
        assertEquals("Hello World!", telegramUpdateMessage.getMessageContent());
    }
}