package folk.tradingbot;

import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.telegram.TelegramClientAutoConfiguration;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

@SpringBootApplication
public class TradingBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingBotApplication.class, args);
        ApplicationContext context = new AnnotationConfigApplicationContext(TelegramClientAutoConfiguration.class);
//        TelegramClient telegramClient = context.getBean(TelegramClient.class);
//        TelegramUser myUser = telegramClient.getMyUser();
//        List<TelegramChat> mainChatList = telegramClient.getMainChatList(20);
//        System.out.println("sd");
//        System.out.println("dfhb");
    }

}
