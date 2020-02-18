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
import smartshare.administrationservice.dto.BucketObjectFromApi;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketObjectRepository;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.UserRepository;

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
    private Consumer<String, BucketObjectFromApi[]> consumer;
    private Producer<String, String> producer;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @MockBean
    private BucketObjectRepository bucketObjectRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BucketRepository bucketRepository;
    @Autowired
    private BucketObjectEventAdministrationService bucketObjectEventAdministrationService;

    @BeforeEach
    public void setUp() {

        //stopping all active kafka listeners
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach( Lifecycle::stop );

        Map<String, Object> producerConfig = new HashMap<>( KafkaTestUtils.producerProps( embeddedKafkaBroker ) );
        producer = new DefaultKafkaProducerFactory<>( producerConfig, new StringSerializer(), new StringSerializer() ).createProducer();
        Map<String, Object> consumerConfig = new HashMap<>( KafkaTestUtils.consumerProps( "accessManagementObjectConsumer", "false", this.embeddedKafkaBroker ) );
        consumer = new DefaultKafkaConsumerFactory<>( consumerConfig, new StringDeserializer(), new JsonDeserializer<>( BucketObjectFromApi[].class ) ).createConsumer();
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


        BucketObjectFromApi bucketObjectFromApi = new BucketObjectFromApi();
        bucketObjectFromApi.setBucketName( "file.server.1" );
        bucketObjectFromApi.setObjectName( "sample.txt" );
        bucketObjectFromApi.setOwnerName( "sethuram" );
        bucketObjectFromApi.setUserName( "ramu" );

        BucketObjectFromApi bucketObjectFromApi2 = new BucketObjectFromApi();
        bucketObjectFromApi2.setBucketName( "file.server.1" );
        bucketObjectFromApi2.setObjectName( "sample2.txt" );
        bucketObjectFromApi2.setOwnerName( "sethuram" );
        bucketObjectFromApi2.setUserName( "ramu" );

        List<BucketObjectFromApi> bucketObjectFromApis = new ArrayList<>();
        bucketObjectFromApis.add( bucketObjectFromApi );
        bucketObjectFromApis.add( bucketObjectFromApi2 );

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        BucketObject bucketObject = new BucketObject( "sample.txt", bucket, new User( "sethuram" ) );
        AccessingUser accessingUser = new AccessingUser( new User( "sethuram" ), bucketObject,
                new ObjectAccess( true, true, true ) );
        bucketObject.setAccessingUsers( Collections.singletonList( accessingUser ) );

        System.out.println( "bucketObjectFromApis---" + bucketObjectFromApis );

        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "emptyBucketObject", objectWriter.writeValueAsString( bucketObjectFromApis ) ) );

        // Assert
        ConsumerRecord<String, BucketObjectFromApi[]> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "emptyBucketObject" );
        assertEquals( singleRecord.value().length, 2 );

        when( bucketObjectRepository.save( any() ) ).thenReturn( bucketObject );
        when( userRepository.findByUserName( any() ) ).thenReturn( new User( "sethuram" ) );
        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );

        bucketObjectEventAdministrationService.createAccessDetailForBucketObject( singleRecord.value()[0] );
        verify( bucketObjectRepository ).save( any() );
        verify( userRepository ).findByUserName( any() );
        verify( bucketRepository ).findByName( any() );

    }

    @Test
    @DisplayName("TEST createAccessDetailForUploadedBucketObjects - SUCCESS")
    void createAccessDetailForUploadedBucketObjects() throws JsonProcessingException {


        BucketObjectFromApi bucketObjectFromApi = new BucketObjectFromApi();
        bucketObjectFromApi.setBucketName( "file.server.1" );
        bucketObjectFromApi.setObjectName( "sample.txt" );
        bucketObjectFromApi.setOwnerName( "sethuram" );
        bucketObjectFromApi.setUserName( "ramu" );

        BucketObjectFromApi bucketObjectFromApi2 = new BucketObjectFromApi();
        bucketObjectFromApi2.setBucketName( "file.server.1" );
        bucketObjectFromApi2.setObjectName( "sample2.txt" );
        bucketObjectFromApi2.setOwnerName( "sethuram" );
        bucketObjectFromApi2.setUserName( "ramu" );

        List<BucketObjectFromApi> bucketObjectFromApis = new ArrayList<>();
        bucketObjectFromApis.add( bucketObjectFromApi );
        bucketObjectFromApis.add( bucketObjectFromApi2 );

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        BucketObject bucketObject = new BucketObject( "sample.txt", bucket, new User( "sethuram" ) );
        AccessingUser accessingUser = new AccessingUser( new User( "sethuram" ), bucketObject,
                new ObjectAccess( true, true, true ) );
        bucketObject.setAccessingUsers( Collections.singletonList( accessingUser ) );

        System.out.println( "bucketObjectFromApis---" + bucketObjectFromApis );

        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "uploadBucketObjects", objectWriter.writeValueAsString( bucketObjectFromApis ) ) );

        // Assert
        ConsumerRecord<String, BucketObjectFromApi[]> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "uploadBucketObjects" );
        assertEquals( singleRecord.value().length, 2 );

        when( bucketObjectRepository.save( any() ) ).thenReturn( bucketObject );
        when( userRepository.findByUserName( any() ) ).thenReturn( new User( "sethuram" ) );
        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );

        bucketObjectEventAdministrationService.createAccessDetailForUploadedBucketObjects( Arrays.asList( singleRecord.value() ) );
        verify( bucketObjectRepository, times( 2 ) ).save( any() );
        verify( userRepository, times( 2 ) ).findByUserName( any() );
        verify( bucketRepository, times( 2 ) ).findByName( any() );

    }

    @Test
    @DisplayName("TEST deleteBucketObject - SUCCESS")
    void deleteBucketObject() throws JsonProcessingException {


        BucketObjectFromApi bucketObjectFromApi = new BucketObjectFromApi();
        bucketObjectFromApi.setBucketName( "file.server.1" );
        bucketObjectFromApi.setObjectName( "sample.txt" );
        bucketObjectFromApi.setOwnerName( "sethuram" );
        bucketObjectFromApi.setUserName( "ramu" );

        BucketObjectFromApi bucketObjectFromApi2 = new BucketObjectFromApi();
        bucketObjectFromApi2.setBucketName( "file.server.1" );
        bucketObjectFromApi2.setObjectName( "sample2.txt" );
        bucketObjectFromApi2.setOwnerName( "sethuram" );
        bucketObjectFromApi2.setUserName( "ramu" );

        List<BucketObjectFromApi> bucketObjectFromApis = new ArrayList<>();
        bucketObjectFromApis.add( bucketObjectFromApi );
        bucketObjectFromApis.add( bucketObjectFromApi2 );

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        BucketObject bucketObject = new BucketObject( "sample.txt", bucket, new User( "sethuram" ) );
        AccessingUser accessingUser = new AccessingUser( new User( "sethuram" ), bucketObject,
                new ObjectAccess( true, true, true ) );
        bucketObject.setAccessingUsers( Collections.singletonList( accessingUser ) );

        System.out.println( "bucketObjectFromApis---" + bucketObjectFromApis );

        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "deleteBucketObjects", objectWriter.writeValueAsString( bucketObjectFromApis ) ) );

        // Assert
        ConsumerRecord<String, BucketObjectFromApi[]> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "deleteBucketObjects" );
        assertEquals( singleRecord.value().length, 2 );

        when( bucketObjectRepository.findByNameAndBucket_Name( any(), any() ) ).thenReturn( bucketObject );

        bucketObjectEventAdministrationService.deleteBucketObjects( Arrays.asList( singleRecord.value() ) );
        verify( bucketObjectRepository, times( 2 ) ).findByNameAndBucket_Name( any(), any() );
        verify( bucketObjectRepository, times( 2 ) ).delete( any() );

    }

}