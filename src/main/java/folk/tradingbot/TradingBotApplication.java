package folk.tradingbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
