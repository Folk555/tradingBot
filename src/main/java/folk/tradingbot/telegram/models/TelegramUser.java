package folk.tradingbot.telegram.models;

import org.drinkless.tdlib.TdApi;

public class TelegramUser {
    private final TdApi.User user;
    public TelegramUser(TdApi.User tdapiUser) {
        this.user = tdapiUser;
    }

    public Long getUserId() {
        return user.id;
    }

}
