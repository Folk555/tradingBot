package folk.tradingbot.telegram.models;

import org.drinkless.tdlib.TdApi;

public class TelegramUpdateMessage {
    private TdApi.UpdateNewMessage updateNewMessage;
    private TdApi.Message message;

    public TelegramUpdateMessage(TdApi.UpdateNewMessage updateNewMessage) {
        this.updateNewMessage = updateNewMessage;
        this.message = updateNewMessage.message;
    }

    public Long getMessageSenderId() {
        TdApi.MessageSender senderId = message.senderId;
        if (senderId instanceof TdApi.MessageSenderUser messageSenderUser) {
            return messageSenderUser.userId;
        } else {
            throw new IllegalArgumentException("Sender ID must be a TdApi.MessageSenderUser");
        }
    }

    public String getMessageContent() {
        if (message.content instanceof TdApi.MessageText messageText) {
            return messageText.text.text;
        } else
            throw new IllegalArgumentException("Message content must be a TdApi.MessageText");
    }
}
