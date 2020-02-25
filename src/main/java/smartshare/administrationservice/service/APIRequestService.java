package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.AccessingUserInfoForApi;
import smartshare.administrationservice.dto.BucketMetadata;
import smartshare.administrationservice.dto.BucketObjectMetadata;
import smartshare.administrationservice.dto.ObjectMetadata;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAccessEntityRepository;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.ObjectAccessEntityRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class APIRequestService {


    private BucketAggregateRepository bucketAggregateRepository;
    private UserAggregateRepository userAggregateRepository;
    private ObjectAccessEntityRepository objectAccessEntityRepository;
    private BucketAccessEntityRepository bucketAccessEntityRepository;

    @Autowired
    public APIRequestService(

            BucketAggregateRepository bucketAggregateRepository,
            UserAggregateRepository userAggregateRepository,
            ObjectAccessEntityRepository objectAccessEntityRepository,
            BucketAccessEntityRepository bucketAccessEntityRepository) {
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.userAggregateRepository = userAggregateRepository;
        this.objectAccessEntityRepository = objectAccessEntityRepository;
        this.bucketAccessEntityRepository = bucketAccessEntityRepository;
    }


    private BucketObjectMetadata mapToReadModel(BucketObjectAggregate bucketObject, UserAggregate user) {
        log.info( "Inside mapToReadModel" );
        BucketObjectMetadata bucketObjectMetadata = null;
        Optional<UserAggregate> owner = userAggregateRepository.findById( bucketObject.getOwnerId() );
        Optional<BucketObjectAccessingUser> accessingUser = bucketObject.getAccessingUsers().stream()
                .filter( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() == user.getUserId() )
                .findFirst();
        if (accessingUser.isPresent() && owner.isPresent()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setOwnerName( owner.get().getUserName() );
            ObjectAccessEntity accessInfo = objectAccessEntityRepository.findById( accessingUser.get().getObjectAccessId() )
                    .orElseGet( () -> objectAccessEntityRepository.findByReadAndWriteAndDelete( false, false, false ) );
            objectMetadata.setAccessingUserInfo( new AccessingUserInfoForApi( user.getUserName(), accessInfo ) );
            bucketObjectMetadata = new BucketObjectMetadata( bucketObject.getBucketObjectName(), objectMetadata );
        }
        return bucketObjectMetadata;
    }


    public List<BucketObjectMetadata> fetchBucketObjectsMetaDataByBucketNameAndUserName(String bucketName, String userName) {
        log.info( "Inside fetchBucketObjectsMetaDataByBucketAndUser" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketName ) );
            UserAggregate user = Objects.requireNonNull( userAggregateRepository.findByUserName( userName ) );
            return bucket.getBucketObjects().stream()
                    .map( bucketObjectAggregate -> mapToReadModel( bucketObjectAggregate, user ) )
                    .filter( Objects::nonNull )
                    .sorted( Comparator.comparing( BucketObjectMetadata::getObjectName ) )
                    .collect( Collectors.toList() );
        } catch (Exception e) {
            log.error( "Exception inside fetchMetaDataForObjectsInS3 service layer ", e );
        }
        return null;
    }


    private BucketMetadata extractBucketMetadata(BucketAggregate bucket, int userId) {
        log.info( "Inside extractBucketMetadata" );
        BucketMetadata bucketMetadata = null;
        Optional<BucketAccessingUser> accessingUser = bucket.getBucketAccessingUsers().stream()
                .filter( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId )
                .findFirst();
        if (accessingUser.isPresent()) {
            BucketAccessEntity accessInfo = bucketAccessEntityRepository.findById( accessingUser.get().getBucketAccessId() )
                    .orElseGet( () -> bucketAccessEntityRepository.findByReadAndWrite( false, false ) );
            bucketMetadata = new BucketMetadata( bucket.getBucketName(), accessInfo );
        }
        return bucketMetadata;
    }

    public List<BucketMetadata> fetchBucketsMetaDataByUserName(String userName) {
        log.info( "Inside fetchMetaDataForBucketsInS3" );
        try {
            UserAggregate user = Objects.requireNonNull( userAggregateRepository.findByUserName( userName ) );
            return bucketAggregateRepository.findAll().stream()
                    .map( bucket -> extractBucketMetadata( bucket, user.getUserId() ) )
                    .filter( Objects::nonNull )
                    .collect( Collectors.toList() );
        } catch (Exception e) {
            log.error( "Exception inside fetchMetaDataForBucketsInS3  ", e );
        }
        return null;
    }

}
