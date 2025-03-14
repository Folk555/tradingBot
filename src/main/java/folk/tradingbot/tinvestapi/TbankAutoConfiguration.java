package folk.tradingbot.tinvestapi;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationPropertiesScan("folk.tradingbot")
@Profile({"prod", "test"})
public class TbankAutoConfiguration {

    @Bean
    public TBankClient tBankClient() {return new TBankClient();}

    @Bean
    public TbankConfigs tbankConfigs() {
        return new TbankConfigs();
    }

}
