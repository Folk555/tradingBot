package folk.tradingbot.telegram.models;

import lombok.Getter;
import org.drinkless.tdlib.TdApi;

public class TelegramUpdateMessage {
    private TdApi.UpdateNewMessage updateNewMessage;
    @Getter
    private TdApi.Message message;

    public TelegramUpdateMessage(TdApi.UpdateNewMessage updateNewMessage) {
        this.updateNewMessage = updateNewMessage;
        this.message = updateNewMessage.message;
    }

    public Long getMessageSenderId() {
        TdApi.MessageSender senderId = message.senderId;
        if (senderId instanceof TdApi.MessageSenderUser messageSenderUser) {
            return messageSenderUser.userId;
        } else if (senderId instanceof TdApi.MessageSenderChat messageSenderChat) {
            return messageSenderChat.chatId;
        } else {
            System.out.println("!!!!!!!!!!!!!ВНИМАНИЕ!!!!!!!");
            System.out.println("Возникло исключение в методе getMessageSenderId");
            throw new IllegalArgumentException("Sender ID must be a TdApi.MessageSenderUser");
        }
    }

    public String getMessageContent() {
        if (message.content instanceof TdApi.MessageText messageText) {
            return messageText.text.text;
        } else if (message.content instanceof TdApi.MessagePhoto messagePhoto) {
            return messagePhoto.caption.text;
        } else {
            System.out.println("!!!!!!!!!!!!!ВНИМАНИЕ!!!!!!!");
            System.out.println("Возникло исключение в методе getMessageContent");
            throw new IllegalArgumentException("Message content must be a TdApi.MessageText");
        }
    }
}
