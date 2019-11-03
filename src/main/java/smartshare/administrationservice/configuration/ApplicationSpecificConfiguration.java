package smartshare.administrationservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartshare.administrationservice.models.Status;

@Configuration
public class ApplicationSpecificConfiguration {

    @Bean
    public Status statusOfOperationPerformed() {
        return new Status();
    }


    @Bean
    public ObjectMapper objectToJsonConverter() {
        return new ObjectMapper();
    }
}
