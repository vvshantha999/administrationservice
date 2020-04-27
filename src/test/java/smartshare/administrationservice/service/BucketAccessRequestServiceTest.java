package smartshare.administrationservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.dto.UserBucketMapping;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BucketAccessRequestServiceTest {


    @MockBean
    private BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository;
    @MockBean
    private BucketAggregateRepository bucketAggregateRepository;
    @MockBean
    private UserAggregateRepository userAggregateRepository;
    @MockBean
    private AdminRoleAggregateRepository adminRoleAggregateRepository;
    @MockBean
    private BucketAccessEntityRepository bucketAccessEntityRepository;

    @Autowired
    private BucketAccessRequestService bucketAccessRequestService;


    @Test
    @DisplayName("TEST createBucketAccessRequest - SUCCESS")
    void createBucketAccessRequest() {

        //setup mock ( if needed verify the mock)

        BucketAccessRequestFromUi bucketAccessRequestFromUi = new BucketAccessRequestFromUi();
        bucketAccessRequestFromUi.setAccess( "read" );
        bucketAccessRequestFromUi.setBucketName( "file.server.1" );
        bucketAccessRequestFromUi.setUserId( 1 );

        BucketAccessRequestEntity bucketAccessRequest = new BucketAccessRequestEntity();

        AdminRoleAggregate adminRole = new AdminRoleAggregate();
        adminRole.setAdminId( 1 );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        bucketAccessRequest.setBucketId( 1 );
        bucketAccessRequest.setUserId( 1 );
        bucketAccessRequest.setBucketAccessId( 2 );
        bucketAccessRequest.setAdminRoleId( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );

        BucketAccessEntity bucketAccessEntity = new BucketAccessEntity();
        bucketAccessEntity.setWrite( false );
        bucketAccessEntity.setRead( false );


        when( bucketAggregateRepository.findByBucketName( any() ) ).thenReturn( bucket );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );
        when( bucketAccessEntityRepository.findByReadAndWrite( any(), any() ) ).thenReturn( bucketAccessEntity );
        when( adminRoleAggregateRepository.findById( any() ) ).thenReturn( Optional.of( adminRole ) );
        when( bucketAccessRequestEntityRepository.save( any() ) ).thenReturn( bucketAccessRequest );

        // execute the call
        Boolean result = bucketAccessRequestService.createBucketAccessRequest( bucketAccessRequestFromUi );

        // assert  the results
        assertEquals( result, true );
        verify( bucketAggregateRepository ).findByBucketName( any() );
        verify( userAggregateRepository ).findByUserName( any() );
        verify( bucketAccessEntityRepository ).findByReadAndWrite( any(), any() );
        verify( adminRoleAggregateRepository ).findById( any() );
        verify( bucketAccessRequestEntityRepository ).save( any() );

    }


    @Test
    @DisplayName("TEST approveBucketAccessRequest - read scenario - SUCCESS")
    void approveBucketAccessRequest_read() {
        //setup mock ( if needed verify the mock)

        BucketAccessRequestEntity bucketAccessRequest = new BucketAccessRequestEntity();

        bucketAccessRequest.setBucketId( 1 );
        bucketAccessRequest.setUserId( 1 );
        bucketAccessRequest.setBucketAccessId( 2 );
        bucketAccessRequest.setAdminRoleId( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );

        BucketAccessEntity bucketAccessEntity = new BucketAccessEntity();
        bucketAccessEntity.setWrite( false );
        bucketAccessEntity.setRead( true );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        when( bucketAccessEntityRepository.findById( bucketAccessRequest.getBucketAccessId() ) )
                .thenReturn( Optional.of( bucketAccessEntity ) );
        when( bucketAggregateRepository.findById( bucketAccessRequest.getBucketId() ) ).thenReturn( Optional.of( bucket ) );

        when( bucketAggregateRepository.save( any() ) ).thenReturn( bucket );

        // execute the call
        Boolean result = bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest );

        // assert  the results

        assertEquals( result, true );
        verify( bucketAggregateRepository ).save( any() );
    }

    @Test
    @DisplayName("TEST approveBucketAccessRequest - write scenario [ new Entry ] - SUCCESS")
    void approveBucketAccessRequest_write() {
        //setup mock ( if needed verify the mock)

        BucketAccessRequestEntity bucketAccessRequest = new BucketAccessRequestEntity();

        bucketAccessRequest.setBucketId( 1 );
        bucketAccessRequest.setUserId( 1 );
        bucketAccessRequest.setBucketAccessId( 2 );
        bucketAccessRequest.setAdminRoleId( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );

        BucketAccessEntity bucketAccessEntity = new BucketAccessEntity();
        bucketAccessEntity.setWrite( true );
        bucketAccessEntity.setRead( false );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        BucketAccessEntity defaultBucketAccessEntity = new BucketAccessEntity();
        defaultBucketAccessEntity.setBucketAccessId( 4 );
        defaultBucketAccessEntity.setWrite( true );
        defaultBucketAccessEntity.setRead( true );

        when( bucketAccessEntityRepository.findById( bucketAccessRequest.getBucketAccessId() ) )
                .thenReturn( Optional.of( bucketAccessEntity ) );
        when( bucketAggregateRepository.findById( bucketAccessRequest.getBucketId() ) ).thenReturn( Optional.of( bucket ) );

        when( userAggregateRepository.findById( any() ) ).thenReturn( Optional.of( user ) );

        when( bucketAggregateRepository.save( any() ) ).thenReturn( bucket );

        when( bucketAccessEntityRepository.findByReadAndWrite( true, true ) ).thenReturn( defaultBucketAccessEntity );

        // execute the call
        Boolean result = bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest );

        // assert  the results

        assertEquals( result, true );
        verify( bucketAggregateRepository ).save( any() );
        verify( bucketAccessEntityRepository ).findById( any() );
        verify( bucketAggregateRepository ).findById( any() );
        verify( userAggregateRepository ).findById( any() );
        verify( bucketAccessEntityRepository ).findByReadAndWrite( any(), any() );
    }
    @Test
    @DisplayName("TEST approveBucketAccessRequest - write scenario [ update Entry ] - SUCCESS")
    void approveBucketAccessRequest_write_update() {
        //setup mock ( if needed verify the mock)

        BucketAccessRequestEntity bucketAccessRequest = new BucketAccessRequestEntity();

        bucketAccessRequest.setBucketId( 1 );
        bucketAccessRequest.setUserId( 1 );
        bucketAccessRequest.setBucketAccessId( 2 );
        bucketAccessRequest.setAdminRoleId( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );

        BucketAccessEntity bucketAccessEntity = new BucketAccessEntity();
        bucketAccessEntity.setWrite( true );
        bucketAccessEntity.setRead( false );

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );
        bucket.addBucketAccessingUsers( 1, 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        BucketAccessEntity defaultBucketAccessEntity = new BucketAccessEntity();
        defaultBucketAccessEntity.setBucketAccessId( 4 );
        defaultBucketAccessEntity.setWrite( true );
        defaultBucketAccessEntity.setRead( true );

        when( bucketAccessEntityRepository.findById( bucketAccessRequest.getBucketAccessId() ) )
                .thenReturn( Optional.of( bucketAccessEntity ) );
        when( bucketAggregateRepository.findById( bucketAccessRequest.getBucketId() ) ).thenReturn( Optional.of( bucket ) );

        when( userAggregateRepository.findById( any() ) ).thenReturn( Optional.of( user ) );

        when( bucketAggregateRepository.save( any() ) ).thenReturn( bucket );

        when( bucketAccessEntityRepository.findByReadAndWrite( true, true ) ).thenReturn( defaultBucketAccessEntity );

        // execute the call
        Boolean result = bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest );

        // assert  the results

        assertEquals( result, true );
        verify( bucketAggregateRepository ).save( any() );
        verify( bucketAccessEntityRepository ).findById( any() );
        verify( bucketAggregateRepository ).findById( any() );
        verify( userAggregateRepository ).findById( any() );
        verify( bucketAccessEntityRepository ).findByReadAndWrite( any(), any() );
    }

    @Test
    @DisplayName("TEST rejectBucketAccessRequest - SUCCESS")
    void rejectBucketAccessRequest() {

        //setup mock ( if needed verify the mock)


        BucketAccessRequestEntity bucketAccessRequest = new BucketAccessRequestEntity();

        bucketAccessRequest.setBucketId( 1 );
        bucketAccessRequest.setUserId( 1 );
        bucketAccessRequest.setBucketAccessId( 2 );
        bucketAccessRequest.setAdminRoleId( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );

        BucketAccessEntity bucketAccessEntity = new BucketAccessEntity();
        bucketAccessEntity.setWrite( true );
        bucketAccessEntity.setRead( false );


        when( bucketAccessRequestEntityRepository.save( any() ) ).thenReturn( bucketAccessRequest );

        // execute the call
        Boolean result = bucketAccessRequestService.rejectBucketAccessRequest( bucketAccessRequest );

        // assert  the results

        assertEquals( result, true );
        verify( bucketAccessRequestEntityRepository ).save( any() );
    }

    @Test
    @DisplayName("TEST addUserToBucketByBucketAdmin - SUCCESS")
    void addUserToBucketByBucketAdmin() {
        //setup mock ( if needed verify the mock)

        UserBucketMapping addUserFromUiToBucket = new UserBucketMapping( "sethu", "file.server.1" );


        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );
        bucket.addBucketAccessingUsers( 1, 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        BucketAccessEntity defaultBucketAccessEntity = new BucketAccessEntity();
        defaultBucketAccessEntity.setBucketAccessId( 4 );
        defaultBucketAccessEntity.setWrite( true );
        defaultBucketAccessEntity.setRead( true );

        when( bucketAggregateRepository.findByBucketName( any() ) ).thenReturn( bucket );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );
        when( bucketAccessEntityRepository.findByReadAndWrite( true, true ) ).thenReturn( defaultBucketAccessEntity );
        // execute the call
        Boolean result = bucketAccessRequestService.addUserToBucketByBucketAdmin( addUserFromUiToBucket );

        // assert  the results

        assertEquals( result, true );
        verify( bucketAggregateRepository ).save( any() );
        verify( bucketAggregateRepository ).findByBucketName( any() );
        verify( bucketAccessEntityRepository ).findByReadAndWrite( any(), any() );
        verify( userAggregateRepository ).findByUserName( any() );

    }

    @Test
    @DisplayName("TEST removeUserFromBucketByBucketAdmin - SUCCESS")
    void removeUserFromBucketByBucketAdmin_success() {
        //setup mock ( if needed verify the mock)

        UserBucketMapping removeUserFromBucket = new UserBucketMapping( "sethu", "file.server.1" );


        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );
        bucket.addBucketAccessingUsers( 1, 1 );
        bucket.addBucketObject( "sethu/", 1 );


        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        BucketAccessEntity defaultBucketAccessEntity = new BucketAccessEntity();
        defaultBucketAccessEntity.setBucketAccessId( 4 );
        defaultBucketAccessEntity.setWrite( true );
        defaultBucketAccessEntity.setRead( true );

        when( bucketAggregateRepository.findByBucketName( any() ) ).thenReturn( bucket );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );
        // execute the call
        Status result = bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );

        assertEquals( result.getValue(), true );
        verify( bucketAggregateRepository ).save( any() );
        verify( bucketAggregateRepository ).findByBucketName( any() );
        verify( userAggregateRepository ).findByUserName( any() );
    }

    @Test
    @DisplayName("TEST removeUserFromBucketByBucketAdmin - MORE OBJECTS")
    void removeUserFromBucketByBucketAdmin_more_objects() {
        //setup mock ( if needed verify the mock)

        UserBucketMapping removeUserFromBucket = new UserBucketMapping( "sethu", "file.server.1" );


        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );
        bucket.addBucketAccessingUsers( 1, 1 );
        bucket.addBucketObject( "sethu/", 1 );
        bucket.addBucketObject( "sethu/sample.txt", 1 );


        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        BucketAccessEntity defaultBucketAccessEntity = new BucketAccessEntity();
        defaultBucketAccessEntity.setBucketAccessId( 4 );
        defaultBucketAccessEntity.setWrite( true );
        defaultBucketAccessEntity.setRead( true );

        when( bucketAggregateRepository.findByBucketName( any() ) ).thenReturn( bucket );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );

        // execute the call
        Status result = bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );
        assertEquals( result.getValue(), false );
        assertEquals( result.getReasonForFailure(), "Either User not exists or user has more Bucket Objects which has to be deleted before removing the user" );
        verify( bucketAggregateRepository ).findByBucketName( any() );
        verify( userAggregateRepository ).findByUserName( any() );
    }

    @Test
    @DisplayName("TEST deleteBucketAccessRequest - SUCCESS")
    void deleteBucketAccessRequest() {

        //setup mock ( if needed verify the mock)

        BucketAccessRequestEntity bucketAccessRequest = new BucketAccessRequestEntity();

        bucketAccessRequest.setBucketId( 1 );
        bucketAccessRequest.setUserId( 1 );
        bucketAccessRequest.setBucketAccessId( 2 );
        bucketAccessRequest.setAdminRoleId( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );


        when( bucketAccessRequestEntityRepository.findById( any() ) ).thenReturn( Optional.of( bucketAccessRequest ) );

        // execute the call
        Status result = bucketAccessRequestService.deleteBucketAccessRequest( bucketAccessRequest );

        assertEquals( result.getValue(), true );
        verify( bucketAccessRequestEntityRepository ).delete( any() );
        verify( bucketAccessRequestEntityRepository ).findById( any() );

    }


}