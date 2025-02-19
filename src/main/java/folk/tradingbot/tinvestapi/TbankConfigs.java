package folk.tradingbot.tinvestapi;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tbank")
public class TbankConfigs {
    @Value("${tbank.token}")
    public String token;
    @Value("${tbank.accountId}")
    public String accountId;
}
