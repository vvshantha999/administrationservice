package smartshare.administrationservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.RemoveUserFromBucket;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.AdminRoleRepository;
import smartshare.administrationservice.repository.BucketAccessRequestRepository;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BucketAccessRequestServiceTest {


    @MockBean
    private BucketAccessRequestRepository bucketAccessRequestRepository;
    @MockBean
    private BucketRepository bucketRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AdminRoleRepository adminRoleRepository;

    @Autowired
    private BucketAccessRequestService bucketAccessRequestService;


    @Test
    @DisplayName("TEST createBucketAccessRequest - SUCCESS")
    void createBucketAccessRequest() {

        //setup mock ( if needed verify the mock)

        BucketAccessRequestFromUi bucketAccessRequestFromUi = new BucketAccessRequestFromUi();
        bucketAccessRequestFromUi.setAccess( "read" );
        bucketAccessRequestFromUi.setBucketName( "file.server.1" );
        bucketAccessRequestFromUi.setUserName( "sethuram" );

        BucketAccessRequest bucketAccessRequest = new BucketAccessRequest();

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucketAccessRequest.setBucket( bucket );
        bucketAccessRequest.setUser( new User( "sethuram" ) );
        bucketAccessRequest.setAccess( new BucketAccess( false, true ) );
        bucketAccessRequest.setAdminAccess( new AdminAccess() );
        bucketAccessRequest.setAdminRole( adminRole );
        bucketAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );

        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( new User( "sethuram" ) );
        when( adminRoleRepository.getOne( any() ) ).thenReturn( adminRole );

        // execute the call
        Boolean result = bucketAccessRequestService.createBucketAccessRequest( bucketAccessRequestFromUi );

        // assert  the results
        assertEquals( result, true );
        verify( bucketAccessRequestRepository ).save( any() );
        verify( userRepository ).findByUserName( any() );
        verify( adminRoleRepository ).getOne( any() );
    }

    @Test
    @DisplayName("TEST approveBucketAccessRequest - SUCCESS")
    void approveBucketAccessRequest() {
        //setup mock ( if needed verify the mock)

        BucketAccessRequest bucketAccessRequest = new BucketAccessRequest();

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucket.setAccessingUsers( Collections.emptyList() );
        bucketAccessRequest.setBucket( bucket );
        bucketAccessRequest.setUser( new User( "sethuram" ) );
        bucketAccessRequest.setAccess( new BucketAccess( false, true ) );
        bucketAccessRequest.setAdminAccess( new AdminAccess() );
        bucketAccessRequest.setAdminRole( adminRole );
        bucketAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );

        when( bucketRepository.save( any() ) ).thenReturn( bucket );

        // execute the call
        Boolean result = bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest );

        // assert  the results

        assertEquals( result, true );
        verify( bucketRepository ).save( any() );
    }

    @Test
    @DisplayName("TEST rejectBucketAccessRequest - SUCCESS")
    void rejectBucketAccessRequest() {

        //setup mock ( if needed verify the mock)

        BucketAccessRequest bucketAccessRequest = new BucketAccessRequest();

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucket.setAccessingUsers( Collections.emptyList() );
        bucketAccessRequest.setBucket( bucket );
        bucketAccessRequest.setUser( new User( "sethuram" ) );
        bucketAccessRequest.setAccess( new BucketAccess( false, true ) );
        bucketAccessRequest.setAdminAccess( new AdminAccess() );
        bucketAccessRequest.setAdminRole( adminRole );
        bucketAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );

        when( bucketAccessRequestRepository.save( any() ) ).thenReturn( bucketAccessRequest );

        // execute the call
        Boolean result = bucketAccessRequestService.rejectBucketAccessRequest( bucketAccessRequest );

        // assert  the results

        assertEquals( result, true );
        verify( bucketAccessRequestRepository ).save( any() );
    }

    @Test
    @DisplayName("TEST addUserToBucketByBucketAdmin - SUCCESS")
    void addUserToBucketByBucketAdmin() {
        //setup mock ( if needed verify the mock)

        AddUserFromUiToBucket addUserFromUiToBucket = new AddUserFromUiToBucket( "sethuram", "file.server.1" );

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucket.setAccessingUsers( Collections.emptyList() );


        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );
        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( new User( "sethuram" ) );


        // execute the call
        Boolean result = bucketAccessRequestService.addUserToBucketByBucketAdmin( addUserFromUiToBucket );

        // assert  the results

        assertEquals( result, true );
        verify( bucketRepository ).save( any() );
        verify( bucketRepository ).findByName( any() );
        verify( userRepository ).findByUserName( any() );

    }

    @Test
    @DisplayName("TEST removeUserFromBucketByBucketAdmin - SUCCESS")
    void removeUserFromBucketByBucketAdmin_success() {
        //setup mock ( if needed verify the mock)

        RemoveUserFromBucket removeUserFromBucket = new RemoveUserFromBucket( "sethuram", "file.server.1" );

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
        bucket.setAccessingUsers( Collections.singletonList( new UserBucketMapping( new User( "sethuram" ), bucket, new BucketAccess( true, false ) ) ) );
        bucket.setObjects( Collections.singletonList( bucketObject ) );

        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );
        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( new User( "sethuram" ) );

        // execute the call
        Status result = bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );

        assertEquals( result.getValue(), true );
        verify( bucketRepository ).save( any() );
        verify( bucketRepository ).findByName( any() );
        verify( userRepository ).findByUserName( any() );
    }

    @Test
    @DisplayName("TEST removeUserFromBucketByBucketAdmin - MORE OBJECTS")
    void removeUserFromBucketByBucketAdmin_more_objects() {
        //setup mock ( if needed verify the mock)

        RemoveUserFromBucket removeUserFromBucket = new RemoveUserFromBucket( "sethuram", "file.server.1" );

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );


        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        BucketObject bucketObject = new BucketObject( "file.server.1/sethuram/", bucket, new User( "sethuram" ) );
        BucketObject bucketObject1 = new BucketObject( "file.server.1/sethuram/sample.txt", bucket, new User( "sethuram" ) );
        AccessingUser accessingUser = new AccessingUser( new User( "sethuram" ), bucketObject,
                new ObjectAccess( true, true, true ) );
        bucketObject.setAccessingUsers( Collections.singletonList( accessingUser ) );
        bucket.setAccessingUsers( Collections.singletonList( new UserBucketMapping( new User( "sethuram" ), bucket, new BucketAccess( true, false ) ) ) );
        List<BucketObject> bucketObjects = new ArrayList<>();
        bucketObjects.add( bucketObject );
        bucketObjects.add( bucketObject1 );
        bucket.setObjects( bucketObjects );


        when( bucketRepository.findByName( any() ) ).thenReturn( bucket );
        when( bucketRepository.save( any() ) ).thenReturn( bucket );
        when( userRepository.findByUserName( any() ) ).thenReturn( new User( "sethuram" ) );

        // execute the call
        Status result = bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );
        assertEquals( result.getValue(), false );
        verify( bucketRepository ).findByName( any() );
        verify( userRepository ).findByUserName( any() );
    }

    @Test
    @DisplayName("TEST deleteBucketAccessRequest - SUCCESS")
    void deleteBucketAccessRequest() {

        //setup mock ( if needed verify the mock)

        BucketAccessRequest bucketAccessRequest = new BucketAccessRequest();

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucket.setAccessingUsers( Collections.emptyList() );

        bucketAccessRequest.setBucket( bucket );
        bucketAccessRequest.setUser( new User( "sethuram" ) );
        bucketAccessRequest.setAccess( new BucketAccess( false, true ) );
        bucketAccessRequest.setAdminAccess( new AdminAccess() );
        bucketAccessRequest.setAdminRole( adminRole );
        bucketAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );


        when( bucketAccessRequestRepository.findById( any() ) ).thenReturn( Optional.of( bucketAccessRequest ) );

        // execute the call
        Status result = bucketAccessRequestService.deleteBucketAccessRequest( bucketAccessRequest );

        assertEquals( result.getValue(), true );
        verify( bucketAccessRequestRepository ).delete( any() );
        verify( bucketAccessRequestRepository ).findById( any() );

    }


}