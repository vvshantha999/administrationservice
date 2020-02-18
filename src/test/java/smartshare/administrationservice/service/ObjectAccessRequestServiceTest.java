package smartshare.administrationservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketObjectRepository;
import smartshare.administrationservice.repository.ObjectAccessRequestRepository;
import smartshare.administrationservice.repository.UserRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ObjectAccessRequestServiceTest {


    @MockBean
    private ObjectAccessRequestRepository objectAccessRequestRepository;
    @MockBean
    private BucketObjectRepository bucketObjectRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectAccessRequestService objectAccessRequestService;


    @Test
    @DisplayName("TEST createObjectAccessRequest - SUCCESS")
    void createObjectAccessRequest() {

        //setup mock ( if needed verify the mock)

        ObjectAccessRequestFromUi objectAccessRequestFromUi = new ObjectAccessRequestFromUi();
        objectAccessRequestFromUi.setAccess( "read" );
        objectAccessRequestFromUi.setBucketName( "file.server.1" );
        objectAccessRequestFromUi.setObjectName( "sample.txt" );
        objectAccessRequestFromUi.setOwnerName( "sethu" );
        objectAccessRequestFromUi.setUserName( "ramu" );

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        BucketObject bucketObject = new BucketObject();
        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucketObject.setBucket( bucket );


        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        objectAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );
        objectAccessRequest.setAccess( new ObjectAccess( "read" ) );
        objectAccessRequest.setUser( new User( "ramu" ) );
        objectAccessRequest.setOwner( new User( "sethu" ) );
        objectAccessRequest.setBucketObject( bucketObject );


        when( bucketObjectRepository.findByName( any() ) ).thenReturn( bucketObject );
        when( userRepository.findByUserName( "sethu" ) ).thenReturn( new User( "sethu" ) );
        when( userRepository.findByUserName( "ramu" ) ).thenReturn( new User( "ramu" ) );
        when( objectAccessRequestRepository.saveAll( any() ) ).thenReturn( Collections.singletonList( objectAccessRequest ) );


        // execute the call
        Boolean result = objectAccessRequestService.createObjectAccessRequest( Collections.singletonList( objectAccessRequestFromUi ) );

        // assert  the results
        assertEquals( result, true );
        verify( bucketObjectRepository ).findByName( any() );


    }


    @Test
    @DisplayName("TEST approveObjectAccessRequest - Update Access - SUCCESS")
    void approveObjectAccessRequest_update_access() {
        //setup mock ( if needed verify the mock)

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        BucketObject bucketObject = new BucketObject();
        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucketObject.setBucket( bucket );
        bucketObject.setAccessingUsers( Collections.singletonList( new AccessingUser(
                new User( "ramu" ), bucketObject, new ObjectAccess( "write" ) ) ) );


        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        objectAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );
        objectAccessRequest.setAccess( new ObjectAccess( "read" ) );
        objectAccessRequest.setUser( new User( "ramu" ) );
        objectAccessRequest.setOwner( new User( "sethu" ) );
        objectAccessRequest.setBucketObject( bucketObject );


        when( objectAccessRequestRepository.save( any() ) ).thenReturn( objectAccessRequest );
        when( bucketObjectRepository.save( any() ) ).thenReturn( bucketObject );


        // execute the call
        Boolean result = objectAccessRequestService.approveObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, true );
        verify( bucketObjectRepository ).save( any() );
        verify( objectAccessRequestRepository ).save( any() );

    }


    @Test
    @DisplayName("TEST approveObjectAccessRequest - New Access - SUCCESS")
    void approveObjectAccessRequest_new_access() {
        //setup mock ( if needed verify the mock)

        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        BucketObject bucketObject = new BucketObject();
        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucketObject.setBucket( bucket );
        bucketObject.setAccessingUsers( Collections.EMPTY_LIST );

        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        objectAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );
        objectAccessRequest.setAccess( new ObjectAccess( "read" ) );
        objectAccessRequest.setUser( new User( "ramu" ) );
        objectAccessRequest.setOwner( new User( "sethu" ) );
        objectAccessRequest.setBucketObject( bucketObject );


        when( objectAccessRequestRepository.save( any() ) ).thenReturn( objectAccessRequest );
        when( bucketObjectRepository.save( any() ) ).thenReturn( bucketObject );


        // execute the call
        Boolean result = objectAccessRequestService.approveObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, true );
        verify( bucketObjectRepository ).save( any() );
        verify( objectAccessRequestRepository ).save( any() );

    }

    @Test
    @DisplayName("TEST rejectObjectAccessRequest  - SUCCESS")
    void rejectObjectAccessRequest() {
        //setup mock ( if needed verify the mock)


        AdminRole adminRole = new AdminRole();
        adminRole.setAdminRoleId( 1L );
        adminRole.setAdminAccess( new AdminAccess() );

        BucketObject bucketObject = new BucketObject();
        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( adminRole );
        bucket.setId( 1L );
        bucketObject.setBucket( bucket );
        bucketObject.setAccessingUsers( Collections.EMPTY_LIST );


        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        objectAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );
        objectAccessRequest.setAccess( new ObjectAccess( "read" ) );
        objectAccessRequest.setUser( new User( "ramu" ) );
        objectAccessRequest.setOwner( new User( "sethu" ) );
        objectAccessRequest.setBucketObject( bucketObject );

        when( objectAccessRequestRepository.save( any() ) ).thenReturn( objectAccessRequest );

        // execute the call
        Boolean result = objectAccessRequestService.rejectObjectAccessRequest( objectAccessRequest );

        // assert  the results
        assertEquals( result, false );
        verify( objectAccessRequestRepository ).save( any() );
    }

}