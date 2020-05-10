package smartshare.administrationservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "core")
public @Data
class CoreServerConfiguration {

    private String hostName;
    private int port;
}
