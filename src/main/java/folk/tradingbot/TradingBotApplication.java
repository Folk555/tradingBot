package folk.tradingbot;

import folk.tradingbot.telegram.TelegramChatListenerService;
import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.trader.repository.TraderPositionRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class TradingBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradingBotApplication.class, args);

        TraderPositionRepo position = context.getBean(TraderPositionRepo.class);
//        folk.tradingbot.trader.dto.TraderPosition entity = new folk.tradingbot.trader.dto.TraderPosition();
//        position.save(entity);
//        TelegramClient telegramClient = context.getBean(TelegramClient.class);
//        TelegramChatListenerService telegramChatListenerService = context.getBean(TelegramChatListenerService.class);
//        Long сигналыОтCashflow = telegramChatListenerService.getTargetChatIdByName().get("СИГНАЛЫ от CASHFLOW");
        //telegramChatListenerService.processMessageDebug(сигналыОтCashflow);
//        String[] lastMessagesTxtFromChat = telegramClient.getLastMessagesTxtFromChat(сигналыОтCashflow, 100);
//        Arrays.stream(lastMessagesTxtFromChat).forEach(System.out::println);
    }

}
