package folk.tradingbot.trader.configuration;

import folk.tradingbot.trader.CashFlowTrader;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConfigurationPropertiesScan("folk.tradingbot")
@EnableScheduling //для кронов
public class TraderAutoConfiguration {

    @Bean
    public CashFlowTrader cashFlowTrader() {
        return new CashFlowTrader();
    }
}
