package folk.tradingbot;

import folk.tradingbot.tinvestapi.TBankClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "folk.tradingbot")
public class TradingBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradingBotApplication.class, args);

        TBankClient tBankClient = context.getBean(TBankClient.class);
        tBankClient.getPortfel();
    }

}
