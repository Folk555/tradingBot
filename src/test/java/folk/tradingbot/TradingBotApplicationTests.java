package folk.tradingbot;

import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.telegram.handlers.TelegramUpdateHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Не забудь добавить переменные среды и путь до td, иначе контекст не соберется
 * можно сделать через IDE, а можно изменить gradle task + builde.gradle.
 * Также может понадобиться готовая БД tdlib
 */
@SpringBootTest
class TradingBotApplicationTests {

    @Autowired
    TelegramClient telegramClient;
    @Autowired
    TelegramUpdateHandler telegramUpdateHandler;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(telegramClient);
        Assertions.assertNotNull(telegramUpdateHandler);
    }

}
