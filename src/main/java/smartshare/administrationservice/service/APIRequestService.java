package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.AccessingUserInfoForApi;
import smartshare.administrationservice.dto.BucketMetadata;
import smartshare.administrationservice.dto.BucketObjectMetadata;
import smartshare.administrationservice.dto.ObjectMetadata;
import smartshare.administrationservice.models.Bucket;
import smartshare.administrationservice.models.BucketObject;
import smartshare.administrationservice.models.UserBucketMapping;
import smartshare.administrationservice.repository.BucketRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class APIRequestService {


    private BucketRepository bucketRepository;

    @Autowired
    public APIRequestService(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    private BucketObjectMetadata dataFormatterForFetchMetaDataForObjectsInS3(BucketObject bucketObject, String userName) {
        log.info( "Inside dataFormatterForFetchMetaDataForObjectsInS3" );
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setOwnerName( bucketObject.getOwner().getUserName() );
        bucketObject.getAccessingUsers().stream()
                .filter( accessingUser -> accessingUser.getUser().getUserName().equals( userName ) )
                .findFirst()
                .ifPresent( accessingUser -> objectMetadata.setAccessingUserInfo( new AccessingUserInfoForApi( userName, accessingUser.getAccess() ) ) );
        return new BucketObjectMetadata( bucketObject.getName(), objectMetadata );

    }

    public List<BucketObjectMetadata> fetchMetaDataForObjectsInGivenBucketForSpecificUser(String bucketName, String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3 service layer" );
        try {
            Bucket bucket = bucketRepository.findByName( bucketName );
            List<BucketObject> bucketObjects = bucket.getObjects();
            if (null != bucketObjects) {
                return bucket.getObjects().stream()
                        .map( bucketObject -> dataFormatterForFetchMetaDataForObjectsInS3( bucketObject, userName ) )
                        .sorted( Comparator.comparing( BucketObjectMetadata::getObjectName ) )
                        .collect( Collectors.toList() );
            }
        } catch (Exception e) {
            log.error( "Exception inside fetchMetaDataForObjectsInS3 service layer ", e );
        }

        return null;
    }

    private Optional<BucketMetadata> extractBucketMetadata(Bucket bucket, String userName) {
        log.info( "Inside extractBucketMetadata" );
        List<UserBucketMapping> accessingUsers = bucket.getAccessingUsers();
        if (accessingUsers != null)
            return accessingUsers.stream()
                    .filter( userBucketMapping -> userBucketMapping.getUser().getUserName().equals( userName ) )
                    .findFirst()
                    .map( userBucketMapping -> new BucketMetadata( bucket.getName(), userName, userBucketMapping.getAccess() ) );
        return Optional.empty();
    }

    public List<BucketMetadata> fetchMetaDataForBucketsInS3(String userName) {
        log.info( "Inside fetchMetaDataForBucketsInS3" );
        try {
            return bucketRepository.findAll().stream()
                    .map( bucket -> extractBucketMetadata( bucket, userName ) )
                    .filter( Optional::isPresent )
                    .map( Optional::get )
                    .collect( Collectors.toList() );
        } catch (Exception e) {
            log.error( "Exception inside fetchMetaDataForBucketsInS3  ", e );
        }
        return null;
    }

}
