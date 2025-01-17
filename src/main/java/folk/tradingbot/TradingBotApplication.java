package folk.tradingbot;

import folk.tradingbot.telegram.TelegramChatListenerService;
import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.telegram.TelegramClientAutoConfiguration;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

@SpringBootApplication
public class TradingBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradingBotApplication.class, args);
//        TelegramClient telegramClient = context.getBean(TelegramClient.class);
//        TelegramChatListenerService telegramChatListenerService = context.getBean(TelegramChatListenerService.class);
//        Long сигналыОтCashflow = telegramChatListenerService.getChatIdByName().get("СИГНАЛЫ от CASHFLOW");
//        telegramChatListenerService.processMessageDebug(сигналыОтCashflow);
    }

}
