package smartshare.administrationservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.BucketObjectAccessRequestEntityRepository;
import smartshare.administrationservice.repository.BucketObjectAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ObjectAccessRequestServiceTest {


    @MockBean
    private BucketObjectAccessRequestEntityRepository objectAccessRequestRepository;
    @MockBean
    private BucketAggregateRepository bucketRepository;
    @MockBean
    private BucketObjectAggregateRepository bucketObjectRepository;

    @MockBean
    private UserAggregateRepository userRepository;

//    @MockBean
//    private ObjectAccessEntityRepository objectAccessEntityRepository;

    @Autowired
    private BucketObjectAccessRequestService objectAccessRequestService;


    @Test
    @DisplayName("TEST createBucketObjectAccessRequests - SUCCESS")
    void createObjectAccessRequest() {

        //setup mock ( if needed verify the mock)

        ObjectAccessRequestFromUi objectAccessRequestFromUi = new ObjectAccessRequestFromUi();
        objectAccessRequestFromUi.setAccess( "read" );
        objectAccessRequestFromUi.setBucketName( "file.server.1" );
        objectAccessRequestFromUi.setObjectName( "sample.txt" );
        objectAccessRequestFromUi.setOwnerName( "sethu" );
        objectAccessRequestFromUi.setUserName( "ramu" );

        UserAggregate owner = new UserAggregate();
        owner.setUserId( 1 );
        owner.setUserName( "sethu" );

        UserAggregate user = new UserAggregate();
        user.setUserId( 2 );
        user.setUserName( "ramu" );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );
        bucket.addBucketObject( "sample.txt", 1 );

        ObjectAccessEntity objectAccessEntity = new ObjectAccessEntity();
        objectAccessEntity.setRead( true );
        objectAccessEntity.setWrite( false );
        objectAccessEntity.setDelete( false );
        objectAccessEntity.setObjectAccessId( 1 );


        BucketObjectAccessRequestEntity objectAccessRequest = new BucketObjectAccessRequestEntity();
        objectAccessRequest.setObjectAccessId( 8 );
        objectAccessRequest.setUserId( 2 );
        objectAccessRequest.setOwnerId( 1 );
        objectAccessRequest.setBucketId( 1 );
        objectAccessRequest.setBucketObjectId( 1 );


        when( bucketRepository.findByBucketName( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( "sethu" ) ).thenReturn( owner );
        when( userRepository.findByUserName( "ramu" ) ).thenReturn( user );
        when( objectAccessRequestRepository.saveAll( any() ) ).thenReturn( Collections.singletonList( objectAccessRequest ) );
//        when(objectAccessEntityRepository.findByReadAndWriteAndDelete( true,false,false )).thenReturn( objectAccessEntity );


        // execute the call
        Boolean result = objectAccessRequestService.createBucketObjectAccessRequests( Collections.singletonList( objectAccessRequestFromUi ) );

        // assert  the results
        assertEquals( result, true );
        verify( bucketRepository ).findByBucketName( any() );
        verify( userRepository, times( 2 ) ).findByUserName( any() );
        verify( objectAccessRequestRepository ).saveAll( any() );
//        verify( objectAccessEntityRepository ).findByReadAndWriteAndDelete( any(),any(),any() );

    }


    @Test
    @DisplayName("TEST deleteBucketObjectAccessRequest  - SUCCESS")
    void deleteBucketObjectAccessRequest() {
        //setup mock ( if needed verify the mock)

        BucketObjectAccessRequestEntity objectAccessRequest = new BucketObjectAccessRequestEntity();
        objectAccessRequest.setObjectAccessId( 8 );
        objectAccessRequest.setUserId( 2 );
        objectAccessRequest.setOwnerId( 1 );
        objectAccessRequest.setBucketId( 1 );
        objectAccessRequest.setBucketObjectId( 1 );

        // execute the call
        Boolean result = objectAccessRequestService.deleteBucketObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, true );
        verify( objectAccessRequestRepository ).delete( any() );
    }


    @Test
    @DisplayName("TEST approveObjectAccessRequest - Update Access - SUCCESS")
    void approveObjectAccessRequest_update_access() {
        //setup mock ( if needed verify the mock)


        ObjectAccessRequestFromUi objectAccessRequestFromUi = new ObjectAccessRequestFromUi();
        objectAccessRequestFromUi.setAccess( "read" );
        objectAccessRequestFromUi.setBucketName( "file.server.1" );
        objectAccessRequestFromUi.setObjectName( "sample.txt" );
        objectAccessRequestFromUi.setOwnerName( "sethu" );
        objectAccessRequestFromUi.setUserName( "ramu" );

        UserAggregate owner = new UserAggregate();
        owner.setUserId( 1 );
        owner.setUserName( "sethu" );

        UserAggregate user = new UserAggregate();
        user.setUserId( 2 );
        user.setUserName( "ramu" );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );


        BucketObjectAggregate bucketObjectAggregate = new BucketObjectAggregate();
        bucketObjectAggregate.setBucket( bucket );
        bucketObjectAggregate.setBucketObjectId( 1 );
        bucketObjectAggregate.setBucketObjectName( "sample.txt" );
        bucketObjectAggregate.setOwnerId( 1 );
        bucketObjectAggregate.addAccessingUser( 2, 5 );

        BucketObjectAccessRequestEntity objectAccessRequest = new BucketObjectAccessRequestEntity();
        objectAccessRequest.setObjectAccessId( 2 );
        objectAccessRequest.setUserId( 2 );
        objectAccessRequest.setOwnerId( 1 );
        objectAccessRequest.setBucketId( 1 );
        objectAccessRequest.setBucketObjectId( 1 );


        when( objectAccessRequestRepository.save( any() ) ).thenReturn( objectAccessRequest );


        when( bucketObjectRepository.save( any() ) ).thenReturn( bucketObjectAggregate );
        when( bucketRepository.findById( any() ) ).thenReturn( Optional.of( bucket ) );
        when( bucketObjectRepository.findByBucketObjectIdAndBucket_BucketId( anyInt(), anyInt() ) ).thenReturn( bucketObjectAggregate );


        // execute the call
        Boolean result = objectAccessRequestService.approveBucketObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, true );
        verify( bucketObjectRepository ).save( any() );
        verify( objectAccessRequestRepository ).save( any() );
        verify( bucketRepository ).findById( any() );
        verify( bucketObjectRepository ).findByBucketObjectIdAndBucket_BucketId( anyInt(), anyInt() );

    }


    @Test
    @DisplayName("TEST approveObjectAccessRequest - New Access - SUCCESS")
    void approveObjectAccessRequest_new_access() {
        //setup mock ( if needed verify the mock)


        ObjectAccessRequestFromUi objectAccessRequestFromUi = new ObjectAccessRequestFromUi();
        objectAccessRequestFromUi.setAccess( "read" );
        objectAccessRequestFromUi.setBucketName( "file.server.1" );
        objectAccessRequestFromUi.setObjectName( "sample.txt" );
        objectAccessRequestFromUi.setOwnerName( "sethu" );
        objectAccessRequestFromUi.setUserName( "ramu" );

        UserAggregate owner = new UserAggregate();
        owner.setUserId( 1 );
        owner.setUserName( "sethu" );

        UserAggregate user = new UserAggregate();
        user.setUserId( 2 );
        user.setUserName( "ramu" );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );


        BucketObjectAggregate bucketObjectAggregate = new BucketObjectAggregate();
        bucketObjectAggregate.setBucket( bucket );
        bucketObjectAggregate.setBucketObjectId( 1 );
        bucketObjectAggregate.setBucketObjectName( "sample.txt" );
        bucketObjectAggregate.setOwnerId( 1 );


        ObjectAccessEntity objectAccessEntity = new ObjectAccessEntity();
        objectAccessEntity.setRead( true );
        objectAccessEntity.setWrite( false );
        objectAccessEntity.setDelete( false );
        objectAccessEntity.setObjectAccessId( 1 );


        BucketObjectAccessRequestEntity objectAccessRequest = new BucketObjectAccessRequestEntity();
        objectAccessRequest.setObjectAccessId( 1 );
        objectAccessRequest.setUserId( 2 );
        objectAccessRequest.setOwnerId( 1 );
        objectAccessRequest.setBucketId( 1 );
        objectAccessRequest.setBucketObjectId( 1 );

        when( objectAccessRequestRepository.save( any() ) ).thenReturn( objectAccessRequest );
        when( bucketObjectRepository.save( any() ) ).thenReturn( bucketObjectAggregate );
        when( bucketRepository.findById( any() ) ).thenReturn( Optional.of( bucket ) );
        when( bucketObjectRepository.findByBucketObjectIdAndBucket_BucketId( anyInt(), anyInt() ) ).thenReturn( bucketObjectAggregate );

        // execute the call
        Boolean result = objectAccessRequestService.approveBucketObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, true );
        verify( bucketObjectRepository ).save( any() );
        verify( objectAccessRequestRepository ).save( any() );
        verify( bucketRepository ).findById( any() );
        verify( bucketObjectRepository ).findByBucketObjectIdAndBucket_BucketId( anyInt(), anyInt() );

    }

    @Test
    @DisplayName("TEST rejectObjectAccessRequest  - SUCCESS")
    void rejectObjectAccessRequest() {
        //setup mock ( if needed verify the mock)

        BucketObjectAccessRequestEntity objectAccessRequest = new BucketObjectAccessRequestEntity();
        objectAccessRequest.setObjectAccessId( 1 );
        objectAccessRequest.setUserId( 2 );
        objectAccessRequest.setOwnerId( 1 );
        objectAccessRequest.setBucketId( 1 );
        objectAccessRequest.setBucketObjectId( 1 );

        when( objectAccessRequestRepository.save( any() ) ).thenReturn( objectAccessRequest );

        // execute the call
        Boolean result = objectAccessRequestService.rejectObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, true );
        verify( objectAccessRequestRepository ).save( any() );
    }

}