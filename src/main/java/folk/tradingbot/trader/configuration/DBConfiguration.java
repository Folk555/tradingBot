package folk.tradingbot.trader.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan("folk.tradingbot")
@EnableJpaRepositories
@EnableTransactionManagement
public class DBConfiguration {

    @ConfigurationProperties("datasource")
    @Bean
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig("hikari.properties");
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

}
