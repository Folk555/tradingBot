package folk.tradingbot.tinvestapi;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan("folk.tradingbot")
public class TbankAutoConfiguration {

    @Bean
    public TBankClient tBankClient() {return new TBankClient();}

    @Bean
    public TbankConfigs tbankConfigs() {
        return new TbankConfigs();
    }

}
