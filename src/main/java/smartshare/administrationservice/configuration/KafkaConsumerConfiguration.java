package smartshare.administrationservice.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import smartshare.administrationservice.dto.BucketObjectFromApi;

import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerConfiguration {

    private Map<String, Object> configurationProperties;

    KafkaConsumerConfiguration() {
        this.configurationProperties = new HashMap<>();
        configurationProperties.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092" );
        configurationProperties.put( ConsumerConfig.GROUP_ID_CONFIG, "accessManagementObjectConsumer" );
        configurationProperties.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class );
        configurationProperties.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class );
    }

    @Bean
    public KafkaConsumer<String, BucketObjectFromApi[]> kafkaBucketObjectConsumer() {
        return new KafkaConsumer<>( configurationProperties, new StringDeserializer(), new JsonDeserializer<>( BucketObjectFromApi[].class ) );
    }
}
