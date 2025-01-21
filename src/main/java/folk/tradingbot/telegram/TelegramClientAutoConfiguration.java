package folk.tradingbot.telegram;

import folk.tradingbot.Utils;
import folk.tradingbot.telegram.configs.TDLibLogger;
import folk.tradingbot.telegram.configs.TelegramConfigs;
import folk.tradingbot.telegram.handlers.TelegramUpdateHandler;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOError;
import java.io.IOException;

@Configuration
@ConfigurationPropertiesScan("folk.tradingbot")
public class TelegramClientAutoConfiguration {

    { //отключение логов
        // set log message handler to handle only fatal errors (0) and plain log messages (-1)
        Client.setLogMessageHandler(0, new TDLibLogger.LogMessageHandler());

        // disable TDLib log and redirect fatal errors and plain log messages to a file
        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
            Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false)));
        } catch (Client.ExecutionException error) {
            System.out.println("!!!!!!!!!!!!!ВНИМАНИЕ!!!!!!!");
            System.out.println("Возникло исключение в static TelegramClientAutoConfiguration");
            throw new IOError(new IOException("Write access to the current directory is required"));
        }
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
