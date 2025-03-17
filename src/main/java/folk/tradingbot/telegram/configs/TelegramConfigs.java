package folk.tradingbot.telegram.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfigs {
    @Value( "${telegram.apiid}" )
    public int apiId;
    @Value( "${telegram.apihash}" )
    public String apiHash;
    @Value( "${telegram.tdlib}" )
    public String tdlib;
}
