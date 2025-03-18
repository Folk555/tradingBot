package folk.tradingbot;

import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.tinvestapi.TBankClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.CountDownLatch;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableJpaRepositories(basePackages = "folk.tradingbot")
public class TradingBotApplication {

    private static final Logger LOGGER = LogManager.getLogger(TradingBotApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradingBotApplication.class, args);
        TelegramClient telegramClient = context.getBean(TelegramClient.class);
        telegramClient.sendMessageToMainChat("Запущен трейдер бот");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Получен сигнал на выключение jvm");
            telegramClient.sendMessageToMainChat("Бот выключается(получен сигнал выкл)");
        }));

        TBankClient tBankClient = context.getBean(TBankClient.class);
        tBankClient.updateSharesDB();

        try {
            LOGGER.info("приложение начинает бесконечно ждать TradingBotApplication.main");
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Приложение было прервано", e);
        }
    }

}
