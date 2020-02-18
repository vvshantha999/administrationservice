package smartshare.administrationservice.service;


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
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.AdminRoleRepository;
import smartshare.administrationservice.repository.BucketRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(
        topics = {"AccessManagement"},
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {"auto.create.topics.enable=true", "log.dir=src/test/out/embedded-kafka"})
class BucketAdministrationServiceTest {


    private Consumer<String, String> consumer;
    private Producer<String, String> producer;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private BucketRepository bucketRepository;

    @MockBean
    private AdminRoleRepository adminRoleRepository;

    @Autowired
    private BucketAdministrationService bucketAdministrationService;


    @BeforeEach
    public void setUp() {

        //stopping all active kafka listeners
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach( Lifecycle::stop );

        Map<String, Object> producerConfig = new HashMap<>( KafkaTestUtils.producerProps( embeddedKafkaBroker ) );
        producer = new DefaultKafkaProducerFactory<>( producerConfig, new StringSerializer(), new StringSerializer() ).createProducer();
        Map<String, Object> consumerConfig = new HashMap<>( KafkaTestUtils.consumerProps( "accessManagementBucketConsumer", "false", this.embeddedKafkaBroker ) );
        consumer = new DefaultKafkaConsumerFactory<>( consumerConfig, new StringDeserializer(), new StringDeserializer() ).createConsumer();
        consumer.subscribe( Collections.singletonList( "AccessManagement" ) );
        consumer.poll( 0 );
    }

    @AfterEach
    public void tearDown() {
        producer.close();
        consumer.close();
    }

    @Test
    @DisplayName("TEST createBucketInAccessManagementDb - SUCCESS")
    void createBucketInAccessManagementDb() {


        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "createBucket", "file.server.1" ) );

        // Assert
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "createBucket" );
        assertEquals( singleRecord.value(), "file.server.1" );

        // mock

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );

        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( adminRoleRepository.getOne( any() ) ).thenReturn( adminRole );

        Bucket result = bucketAdministrationService.createBucketInAccessManagementDb( singleRecord.value() );
        verify( bucketRepository ).save( any() );


    }

    @Test
    @DisplayName("TEST deleteBucketInAccessManagementDb - without bucket objects - SUCCESS")
    void deleteBucketInAccessManagementDb_without_bucket_objects() {

        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "deleteBucket", "file.server.1" ) );

        // Assert
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "deleteBucket" );
        assertEquals( singleRecord.value(), "file.server.1" );

        // mock

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );

        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );

        Status result = bucketAdministrationService.deleteBucketInAccessManagementDb( singleRecord.value() );
        assertEquals( result.getValue(), true );
        verify( bucketRepository ).delete( any() );
    }


    @Test
    @DisplayName("TEST deleteBucketInAccessManagementDb - with bucket objects- SUCCESS")
    void deleteBucketInAccessManagementDb_with_bucket_objects() {

        // Act
        producer.send( new ProducerRecord<>( "AccessManagement", "deleteBucket", "file.server.1" ) );

        // Assert
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord( consumer, "AccessManagement" );
        assertNotNull( singleRecord );
        assertEquals( singleRecord.key(), "deleteBucket" );
        assertEquals( singleRecord.value(), "file.server.1" );

        // mock

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        BucketObject bucketObject = new BucketObject( "file.server.1/sethuram/", bucket, new User( "sethuram" ) );
        AccessingUser accessingUser = new AccessingUser( new User( "sethuram" ), bucketObject,
                new ObjectAccess( true, true, true ) );
        bucketObject.setAccessingUsers( Collections.singletonList( accessingUser ) );
        bucket.setObjects( Collections.singletonList( bucketObject ) );


        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );

        Status result = bucketAdministrationService.deleteBucketInAccessManagementDb( singleRecord.value() );
        assertEquals( result.getValue(), false );
        assertEquals( result.getReasonForFailure(), "Please delete Bucket Objects before deleting the bucket" );
        verify( bucketRepository ).findByName( any() );
    }

}