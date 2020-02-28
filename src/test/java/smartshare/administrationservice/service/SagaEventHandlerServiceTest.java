package smartshare.administrationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.Lifecycle;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.dto.BucketObjectEvent;
import smartshare.administrationservice.dto.SagaEvent;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(
        topics = {"sagaAccess", "sagaAccessResult"},
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {"auto.create.topics.enable=true", "log.dir=src/test/out/embedded-kafka"})
class SagaEventHandlerServiceTest {


    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private Consumer<String, SagaEvent> consumer;
    private Producer<String, String> producer;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private SagaEventHandlerService sagaEventHandlerService;

    @MockBean
    private BucketObjectAdministrationService bucketObjectAdministrationService;

    @BeforeEach
    public void setUp() {


        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach( Lifecycle::stop );

        Map<String, Object> producerConfig = new HashMap<>( KafkaTestUtils.producerProps( embeddedKafkaBroker ) );
        producer = new DefaultKafkaProducerFactory<>( producerConfig, new StringSerializer(), new StringSerializer() ).createProducer();
        Map<String, Object> consumerConfig = new HashMap<>( KafkaTestUtils.consumerProps( "sagaEventConsumer", "false", this.embeddedKafkaBroker ) );
        consumer = new DefaultKafkaConsumerFactory<>( consumerConfig, new StringDeserializer(), new JsonDeserializer<>( SagaEvent.class ) ).createConsumer();
        consumer.subscribe( Arrays.asList( "sagaAccess", "sagaAccessResult" ) );
        consumer.poll( 0 );
    }

    @AfterEach
    public void tearDown() {
        producer.close();
        consumer.close();
    }


    @Test
    void consume() throws JsonProcessingException {
        BucketObjectEvent bucketObjectFromApi = new BucketObjectEvent();
        bucketObjectFromApi.setBucketName( "file.server.1" );
        bucketObjectFromApi.setObjectName( "sample.txt" );
        bucketObjectFromApi.setOwnerName( "sethuram" );
        bucketObjectFromApi.setUserName( "ramu" );

        BucketObjectEvent bucketObjectFromApi2 = new BucketObjectEvent();
        bucketObjectFromApi2.setBucketName( "file.server.1" );
        bucketObjectFromApi2.setObjectName( "sample2.txt" );
        bucketObjectFromApi2.setOwnerName( "sethuram" );
        bucketObjectFromApi2.setUserName( "ramu" );

        List<BucketObjectEvent> bucketObjectFromApis = new ArrayList<>();
        bucketObjectFromApis.add( bucketObjectFromApi );
        bucketObjectFromApis.add( bucketObjectFromApi2 );
        SagaEvent sagaEvent = new SagaEvent();
        sagaEvent.setEventId( "1" );
        sagaEvent.setObjects( bucketObjectFromApis );

        //Reply Record

        BucketObjectEvent bucketObjectFromApiResult = bucketObjectFromApi.duplicate();
        bucketObjectFromApiResult.setStatus( "completed" );

        BucketObjectEvent bucketObjectFromApi2Result = bucketObjectFromApi2.duplicate();
        bucketObjectFromApi2Result.setStatus( "completed" );

        List<BucketObjectEvent> bucketObjectFromApisResult = new ArrayList<>();
        bucketObjectFromApisResult.add( bucketObjectFromApiResult );
        bucketObjectFromApisResult.add( bucketObjectFromApi2Result );

        when( bucketObjectAdministrationService.createAccessDetailForUploadedBucketObjects( any() ) ).thenReturn( bucketObjectFromApisResult );

        // Act
        producer.send( new ProducerRecord<>( "sagaAccess", "create", objectWriter.writeValueAsString( sagaEvent ) ) );

        // Assert
        ConsumerRecord<String, SagaEvent> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "sagaAccess" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "create" );
        assertEquals( singleRecord.value().getObjects().size(), 2 );
        SagaEvent result = sagaEventHandlerService.consume( singleRecord.value(), singleRecord );

        // Reply
        producer.send( new ProducerRecord<>( "sagaAccessResult", objectWriter.writeValueAsString( result ) ) );

        ConsumerRecord<String, SagaEvent> replyRecord = KafkaTestUtils.getSingleRecord( consumer, "sagaAccessResult" );
        assertNotNull( replyRecord );
        assertEquals( replyRecord.value().getObjects().size(), 2 );

        verify( bucketObjectAdministrationService ).createAccessDetailForUploadedBucketObjects( any() );

    }
}