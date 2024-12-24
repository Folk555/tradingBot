package folk.tradingbot.telegram.models;

import org.drinkless.tdlib.TdApi;

public class TelegramChat {

    private final TdApi.Chat tdapiChat;

    public TelegramChat(TdApi.Chat tdapiChat) {
        this.tdapiChat = tdapiChat;
    }

    public Long getId() {
        return tdapiChat.id;
    }

    public String getChatName() {
        return tdapiChat.title;
    }
}
