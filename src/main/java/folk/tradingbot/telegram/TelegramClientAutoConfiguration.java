package folk.tradingbot.telegram;

import folk.tradingbot.Utils;
import folk.tradingbot.telegram.configs.TDLibLogger;
import folk.tradingbot.telegram.configs.TelegramConfigs;
import folk.tradingbot.telegram.handlers.TelegramUpdateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOError;
import java.io.IOException;

@Configuration
@ConfigurationPropertiesScan("folk.tradingbot")
@Profile({"prod", "test"})
public class TelegramClientAutoConfiguration {
    private static Logger LOGGER = LogManager.getLogger(TelegramClientAutoConfiguration.class);

    {
        try {
            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().startsWith("windows")) {
                System.loadLibrary("libcrypto-3-x64");
                System.loadLibrary("libssl-3-x64");
                System.loadLibrary("zlib1");
            }
            System.loadLibrary("tdjni");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

        //отключение логов
        // set log message handler to handle only fatal errors (0) and plain log messages (-1)
        LOGGER.trace("конструктор statiс TelegramClientAutoConfiguration");
        Client.setLogMessageHandler(0, new TDLibLogger.LogMessageHandler());

        // disable TDLib log and redirect fatal errors and plain log messages to a file
        LOGGER.trace("Выключаем логи TDLIB");
        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
            Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false)));
        } catch (Client.ExecutionException error) {
            LOGGER.error("Возникло исключение в static TelegramClientAutoConfiguration");
            throw new IOError(new IOException("Write access to the current directory is required"));
        }
        LOGGER.trace("Статик конструктор TelegramClientAutoConfiguration отработал");
    }

    @Bean
    public TelegramClient telegramClient(TelegramUpdateHandler telegramUpdateHandler) {
        TelegramClient telegramClient = new TelegramClient(telegramUpdateHandler);
        telegramUpdateHandler.setTelegramClient(telegramClient);
        while (!telegramUpdateHandler.isAuthorizated()) { //нет смысла отдавать клиент если он еще не авторизован
            Utils.sleep(1);
        }
        return telegramClient;
    }

    @Bean
    public TelegramUpdateHandler telegramUpdateHandler() {
        return new TelegramUpdateHandler();
    }

    @Bean
    public TelegramChatListenerService telegramChatListenerService(TelegramUpdateHandler telegramUpdateHandler) {
        TelegramChatListenerService telegramChatListenerService = new TelegramChatListenerService();
        telegramUpdateHandler.setChatListenerService(telegramChatListenerService);
        return telegramChatListenerService;
    }

    @Bean
    public TelegramConfigs telegramConfigs() {
        return new TelegramConfigs();
    }

}
