package folk.tradingbot.telegram;

import folk.tradingbot.telegram.models.TelegramUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TelegramClientInegrTest {

    @Autowired
    private TelegramClient telegramClientBean;

    @Test
    void getMyUser() {
        TelegramUser myUser = telegramClientBean.getMyUser();

        Assertions.assertNotNull(myUser);
        Assertions.assertEquals(458696774, myUser.getUserId());
    }

}