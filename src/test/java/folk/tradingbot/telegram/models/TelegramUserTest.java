package folk.tradingbot.telegram.models;

import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramUserTest {

    private TelegramUser telegramUser;

    {
        TdApi.User tdapiUser = new TdApi.User();
        tdapiUser.id = 800;
        telegramUser = new TelegramUser(tdapiUser);
    }

    @Test
    void getUserId() {
        assertEquals(800, telegramUser.getUserId());
    }
}