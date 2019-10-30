package smartshare.administrationservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartshare.administrationservice.models.Status;

@Configuration
public class ApplicationSpecificConfiguration {

    @Bean
    public Status statusOfOperationPerformed() {
        return new Status();
    }
}
