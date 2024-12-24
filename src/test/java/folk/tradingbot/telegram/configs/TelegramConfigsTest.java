package folk.tradingbot.telegram.configs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest
@Timeout(value = 2, unit = SECONDS)
class TelegramConfigsTest {

    @Autowired
    private TelegramConfigs telegramConfigs;

    @Test
    public void apiId() {
        Assertions.assertTrue(String.valueOf(telegramConfigs.apiId).contains("894"),
                "apiId: " + telegramConfigs.apiId);
    }

    @Test
    public void apiHash() {
        Assertions.assertTrue(String.valueOf(telegramConfigs.apiHash).contains("a5909"),
                "apiHash: " + telegramConfigs.apiHash);

    }

    @Test
    public void tdlib() {
        Assertions.assertTrue(String.valueOf(telegramConfigs.tdlib).contains("tdlib"),
                "tdlib: " + telegramConfigs.tdlib);
    }

    @Test
    public void tradingChatName() {
        Assertions.assertTrue(String.valueOf(telegramConfigs.tradingChatName).contains("СИГНАЛЫ"),
                "tradingChatName: " + telegramConfigs.tradingChatName);
    }
}