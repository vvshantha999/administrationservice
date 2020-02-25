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
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.models.BucketObjectAggregate;
import smartshare.administrationservice.models.UserAggregate;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class APIRequestServiceTest {

    @MockBean
    private BucketAggregateRepository bucketRepository;

    @MockBean
    private UserAggregateRepository userAggregateRepository;

    @Autowired
    private APIRequestService apiRequestService;


    @Test
    @DisplayName("TEST fetchMetaDataForObjectsInGivenBucketForSpecificUser - SUCCESS")
    void fetchMetaDataForObjectsInGivenBucketForSpecificUser() {

        //setup mock ( if needed verify the mock)

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 2 );


        BucketObjectAggregate bucketObject = new BucketObjectAggregate( "sample.txt", bucket, 2 );
        bucketObject.addAccessingUser( 3, 4 );

        BucketObjectAggregate bucketObject1 = new BucketObjectAggregate( "sample1.txt", bucket, 2 );
        bucketObject1.addAccessingUser( 3, 4 );

        BucketObjectAggregate bucketObject2 = new BucketObjectAggregate( "sample2.txt", bucket, 2 );
        bucketObject2.addAccessingUser( 3, 4 );


        Set<BucketObjectAggregate> bucketObjects = new HashSet<>();
        bucketObjects.add( bucketObject );
        bucketObjects.add( bucketObject1 );
        bucketObjects.add( bucketObject2 );

        bucket.setBucketObjects( bucketObjects );
        UserAggregate user = new UserAggregate();
        user.setUserId( 3 );
        user.setUserName( "sethu" );


        when( bucketRepository.findByBucketName( anyString() ) ).thenReturn( bucket );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );
        when( userAggregateRepository.findById( any() ) ).thenReturn( Optional.of( user ) );

        // execute the call
        List<BucketObjectMetadata> result = apiRequestService.fetchBucketObjectsMetaDataByBucketNameAndUserName( "file.server.1", "sethu" );

        // assert  the results
        assertAll(
                () -> assertEquals( result.get( 0 ).getObjectName(), "sample.txt" ),
                () -> assertEquals( result.get( 0 ).getObjectMetadata().getOwnerName(), "sethu" ),
                () -> assertEquals( result.get( 0 ).getObjectMetadata().getAccessingUserInfo().getRead(), false )
        );
        verify( bucketRepository ).findByBucketName( anyString() );
        verify( userAggregateRepository ).findByUserName( anyString() );
        verify( userAggregateRepository, times( 3 ) ).findById( any() );

    }

    @Test
    @DisplayName("TEST fetchMetaDataForBucketsInS3 - EMPTY FIND ALL RESULT")
    void fetchMetaDataForBucketsInS3_empty_find_all() {
        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );


        //setup mock ( if needed verify the mock)
        when( bucketRepository.findAll() ).thenReturn( Collections.EMPTY_LIST );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );

        // execute the call
        List<BucketMetadata> result = apiRequestService.fetchBucketsMetaDataByUserName( "sethuram" );
        assertEquals( result, Collections.EMPTY_LIST );
        verify( bucketRepository ).findAll();
    }


    @Test
    @DisplayName("TEST fetchMetaDataForBucketsInS3 - SUCCESS")
    void fetchMetaDataForBucketsInS3() {
        //setup mock ( if needed verify the mock)

        BucketAggregate bucket = new BucketAggregate();
        bucket.setBucketName( "file.server.1" );
        bucket.setAdminId( 1 );
        bucket.setBucketId( 1 );

        UserAggregate user = new UserAggregate();
        user.setUserId( 1 );
        user.setUserName( "sethu" );

        bucket.addBucketAccessingUsers( 1, 3 );


        BucketAggregate bucket2 = new BucketAggregate();
        bucket2.setBucketName( "file.server.2" );
        bucket2.setAdminId( 1 );
        bucket2.setBucketId( 2 );

        bucket2.addBucketAccessingUsers( 1, 2 );

        BucketAggregate bucket3 = new BucketAggregate();
        bucket3.setBucketName( "file.server.3" );
        bucket3.setAdminId( 1 );
        bucket3.setBucketId( 3 );

        bucket3.addBucketAccessingUsers( 1, 4 );

        List<BucketAggregate> buckets = new ArrayList<>();
        buckets.add( bucket );
        buckets.add( bucket2 );
        buckets.add( bucket3 );

        when( bucketRepository.findAll() ).thenReturn( buckets );
        when( userAggregateRepository.findByUserName( any() ) ).thenReturn( user );

        // execute the call
        List<BucketMetadata> result = apiRequestService.fetchBucketsMetaDataByUserName( "sethu" );

        // assert  the results
        assertAll(
                () -> assertEquals( result.get( 0 ).getBucketName(), "file.server.1" ),
                () -> assertEquals( result.get( 1 ).getRead(), false )
        );
        verify( bucketRepository ).findAll();
    }


}