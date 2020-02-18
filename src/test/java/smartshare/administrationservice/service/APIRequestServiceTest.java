package smartshare.administrationservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import smartshare.administrationservice.dto.BucketMetadata;
import smartshare.administrationservice.dto.BucketObjectMetadata;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class APIRequestServiceTest {

    @MockBean
    private BucketRepository bucketRepository;

    @Autowired
    private APIRequestService apiRequestService;


    @Test
    @DisplayName("TEST fetchMetaDataForObjectsInGivenBucketForSpecificUser - SUCCESS")
    void fetchMetaDataForObjectsInGivenBucketForSpecificUser() {

        //setup mock ( if needed verify the mock)

        Bucket bucket = new Bucket();
        bucket.setName( "file.server.1" );
        bucket.setAdminRole( new AdminRole() );
        bucket.setId( 1L );

        BucketObject bucketObject = new BucketObject( "sample.txt", bucket, new User( "sethuram" ) );
        ObjectAccess objectAccess = new ObjectAccess( false, true, false );
        AccessingUser accessingUser = new AccessingUser( new User( "sethuram" ), bucketObject, objectAccess );
        bucketObject.setAccessingUsers( Collections.singletonList( accessingUser ) );

        BucketObject bucketObject1 = new BucketObject( "sample1.txt", bucket, new User( "sethuram" ) );
        ObjectAccess objectAccess1 = new ObjectAccess( true, true, false );
        AccessingUser accessingUser1 = new AccessingUser( new User( "sethuram" ), bucketObject1, objectAccess1 );
        bucketObject1.setAccessingUsers( Collections.singletonList( accessingUser1 ) );

        BucketObject bucketObject2 = new BucketObject( "sample2.txt", bucket, new User( "sethuram" ) );
        ObjectAccess objectAccess2 = new ObjectAccess( true, false, false );
        AccessingUser accessingUser2 = new AccessingUser( new User( "sethuram" ), bucketObject2, objectAccess2 );
        bucketObject2.setAccessingUsers( Collections.singletonList( accessingUser2 ) );

        List<BucketObject> bucketObjects = new ArrayList<>();
        bucketObjects.add( bucketObject );
        bucketObjects.add( bucketObject1 );
        bucketObjects.add( bucketObject2 );

        bucket.setObjects( bucketObjects );

        when( bucketRepository.findByName( anyString() ) ).thenReturn( bucket );

        // execute the call
        List<BucketObjectMetadata> result = apiRequestService.fetchMetaDataForObjectsInGivenBucketForSpecificUser( "file.server.1", "sethuram" );

        // assert  the results
        assertAll(
                () -> assertEquals( result.get( 0 ).getObjectName(), "sample.txt" ),
                () -> assertEquals( result.get( 0 ).getObjectMetadata().getOwnerName(), "sethuram" ),
                () -> assertEquals( result.get( 0 ).getObjectMetadata().getAccessingUserInfo().getRead(), false )
        );
        verify( bucketRepository ).findByName( anyString() );

    }

    @Test
    @DisplayName("TEST fetchMetaDataForBucketsInS3 - EMPTY FIND ALL RESULT")
    void fetchMetaDataForBucketsInS3_empty_find_all() {
        //setup mock ( if needed verify the mock)
        when( bucketRepository.findAll() ).thenReturn( Collections.EMPTY_LIST );

        // execute the call
        List<BucketMetadata> result = apiRequestService.fetchMetaDataForBucketsInS3( "sethuram" );
        assertEquals( result, Collections.EMPTY_LIST );
        verify( bucketRepository ).findAll();
    }


    @Test
    @DisplayName("TEST fetchMetaDataForBucketsInS3 - SUCCESS")
    void fetchMetaDataForBucketsInS3() {
        //setup mock ( if needed verify the mock)

        Bucket bucket1 = new Bucket();
        bucket1.setName( "file.server.1" );
        bucket1.setAdminRole( new AdminRole() );
        bucket1.setId( 1L );
        User user = new User( "sethuram" );
        UserBucketMapping userBucketMapping = new UserBucketMapping( user, bucket1, new BucketAccess( true, true ) );
        bucket1.setAccessingUsers( Collections.singletonList( userBucketMapping ) );

        Bucket bucket2 = new Bucket();
        bucket2.setName( "file.server.2" );
        bucket2.setAdminRole( new AdminRole() );
        bucket2.setId( 2L );
        UserBucketMapping userBucketMapping2 = new UserBucketMapping( user, bucket1, new BucketAccess( false, true ) );
        bucket2.setAccessingUsers( Collections.singletonList( userBucketMapping2 ) );

        Bucket bucket3 = new Bucket();
        bucket3.setName( "file.server.3" );
        bucket3.setAdminRole( new AdminRole() );
        bucket3.setId( 3L );
        bucket3.setAccessingUsers( Collections.singletonList( userBucketMapping ) );

        List<Bucket> buckets = new ArrayList<>();
        buckets.add( bucket1 );
        buckets.add( bucket2 );
        buckets.add( bucket3 );

        when( bucketRepository.findAll() ).thenReturn( buckets );

        // execute the call
        List<BucketMetadata> result = apiRequestService.fetchMetaDataForBucketsInS3( "sethuram" );

        // assert  the results
        assertAll(
                () -> assertEquals( result.get( 0 ).getBucketName(), "file.server.1" ),
                () -> assertEquals( result.get( 1 ).getRead(), false )
        );
        verify( bucketRepository ).findAll();
    }


}