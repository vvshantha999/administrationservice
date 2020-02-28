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
import org.junit.jupiter.api.DisplayName;
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
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.models.UserAggregate;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(
        topics = {"AccessManagement"},
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {"auto.create.topics.enable=true", "log.dir=src/test/out/embedded-kafka"})
class BucketObjectEventAdministrationServiceTest {

    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private Consumer<String, BucketObjectEvent[]> consumer;
    private Producer<String, String> producer;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private UserAggregateRepository userRepository;
    @MockBean
    private BucketAggregateRepository bucketRepository;
    @Autowired
    private BucketObjectAdministrationService bucketObjectAdministrationService;

    @BeforeEach
    public void setUp() {

        //stopping all active kafka listeners
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach( Lifecycle::stop );

        Map<String, Object> producerConfig = new HashMap<>( KafkaTestUtils.producerProps( embeddedKafkaBroker ) );
        producer = new DefaultKafkaProducerFactory<>( producerConfig, new StringSerializer(), new StringSerializer() ).createProducer();
        Map<String, Object> consumerConfig = new HashMap<>( KafkaTestUtils.consumerProps( "accessManagementObjectConsumer", "false", this.embeddedKafkaBroker ) );
        consumer = new DefaultKafkaConsumerFactory<>( consumerConfig, new StringDeserializer(), new JsonDeserializer<>( BucketObjectEvent[].class ) ).createConsumer();
        consumer.subscribe( Collections.singletonList( "AccessManagement" ) );
        consumer.poll( 0 );
    }

    @AfterEach
    public void tearDown() {
        producer.close();
        consumer.close();
    }


    @Test
    @DisplayName("TEST createAccessDetailForBucketObject - SUCCESS")
    void createAccessDetailForBucketObject() throws JsonProcessingException {


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


        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        bucket.addBucketAccessingUsers( 1, 3 );


        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "emptyBucketObject", objectWriter.writeValueAsString( bucketObjectFromApis ) ) );

        // Assert
        ConsumerRecord<String, BucketObjectEvent[]> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "emptyBucketObject" );
        assertEquals( singleRecord.value().length, 2 );


        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( user );
        when( bucketRepository.findByBucketName( any() ) ).thenReturn( bucket );


        bucketObjectAdministrationService.createAccessDetailForBucketObject( singleRecord.value()[0] );
        verify( userRepository ).findByUserName( any() );
        verify( bucketRepository ).findByBucketName( any() );
        verify( bucketRepository ).save( any() );


    }

    @Test
    @DisplayName("TEST createAccessDetailForUploadedBucketObjects - SUCCESS")
    void createAccessDetailForUploadedBucketObjects() throws JsonProcessingException {


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


        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        bucket.addBucketAccessingUsers( 1, 3 );


        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "uploadBucketObjects", objectWriter.writeValueAsString( bucketObjectFromApis ) ) );

        // Assert
        ConsumerRecord<String, BucketObjectEvent[]> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "uploadBucketObjects" );
        assertEquals( singleRecord.value().length, 2 );


        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( user );
        when( bucketRepository.findByBucketName( any() ) ).thenReturn( bucket );


        bucketObjectAdministrationService.createAccessDetailForUploadedBucketObjects( Arrays.asList( singleRecord.value() ) );
        verify( userRepository, times( 2 ) ).findByUserName( any() );
        verify( bucketRepository, times( 2 ) ).findByBucketName( any() );
        verify( bucketRepository, times( 2 ) ).save( any() );


    }

    @Test
    @DisplayName("TEST deleteBucketObject - SUCCESS")
    void deleteBucketObject() throws JsonProcessingException {


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


        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        bucket.addBucketObject( "sample.txt", 1 );
        bucket.addBucketObject( "sample2.txt", 1 );
        bucket.addBucketAccessingUsers( 1, 3 );


        System.out.println( "bucketObjectFromApis---" + bucketObjectFromApis );

        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "deleteBucketObjects", objectWriter.writeValueAsString( bucketObjectFromApis ) ) );

        // Assert
        ConsumerRecord<String, BucketObjectEvent[]> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "deleteBucketObjects" );
        assertEquals( singleRecord.value().length, 2 );

        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( user );
        when( bucketRepository.findByBucketName( any() ) ).thenReturn( bucket );

        bucketObjectAdministrationService.deleteBucketObjects( Arrays.asList( singleRecord.value() ) );
        verify( userRepository, times( 2 ) ).findByUserName( any() );
        verify( bucketRepository, times( 2 ) ).findByBucketName( any() );
        verify( bucketRepository, times( 2 ) ).save( any() );

    }

}