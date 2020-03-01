package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.BucketObjectEvent;
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.models.UserAggregate;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BucketObjectAdministrationService {

    private BucketAggregateRepository bucketAggregateRepository;
    private UserAggregateRepository userAggregateRepository;


    @Autowired
    BucketObjectAdministrationService(

            BucketAggregateRepository bucketAggregateRepository,
            UserAggregateRepository userAggregateRepository
    ) {
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.userAggregateRepository = userAggregateRepository;
    }


    private BucketObjectEvent deleteBucketObject(BucketObjectEvent bucketObjectFromApi) {
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketObjectFromApi.getBucketName() ) );
            UserAggregate owner = Objects.requireNonNull( userAggregateRepository.findByUserName( bucketObjectFromApi.getOwnerName() ) );
            if (bucket.removeBucketObject( bucketObjectFromApi.getObjectName(), owner.getUserId() )) {
                bucketAggregateRepository.save( bucket );
                log.info( bucketObjectFromApi.getObjectName() + " access details deleted successfully" );
                bucketObjectFromApi.setStatus( StatusConstants.SUCCESS.toString() );
            }
            log.info( bucketObjectFromApi.getObjectName() + " access details deletion  failed as the bucket object was not found" );
        } catch (Exception e) {
            log.error( "Exception while deleting the object " + bucketObjectFromApi + " " + e );
        }
        bucketObjectFromApi.setStatus( StatusConstants.FAILED.toString() );
        return bucketObjectFromApi;
    }

    public List<BucketObjectEvent> deleteBucketObjects(List<BucketObjectEvent> objectsToBeDeleted) {
        log.info( "Inside deleteGivenObjectsInDb" );

        return objectsToBeDeleted.stream()
                .map( this::deleteBucketObject )
                .collect( Collectors.toList() );
    }


    public BucketObjectEvent createAccessDetailForBucketObject(BucketObjectEvent bucketObjectFromApi) {
        log.info( "Inside createAccessDetailForBucketObject" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketObjectFromApi.getBucketName() ) );
            UserAggregate owner = Objects.requireNonNull( userAggregateRepository.findByUserName( bucketObjectFromApi.getOwnerName() ) );
            if (bucket.isUserExistsInBucket( owner.getUserId() )) {
                bucket.addBucketObject( bucketObjectFromApi.getObjectName(), owner.getUserId() );
                bucketAggregateRepository.save( bucket );
                log.info( bucketObjectFromApi.toString() + " access details created successfully" );
                bucketObjectFromApi.setStatus( StatusConstants.SUCCESS.toString() );
            } else
                log.error( "User has not Authorized to create Bucket Object in this bucket" );
        } catch (Exception e) {
            log.error( "Exception while createAccessDetailForGivenBucketObject " + e );
        }
        bucketObjectFromApi.setStatus( StatusConstants.FAILED.toString() );
        return bucketObjectFromApi;
    }

    public List<BucketObjectEvent> createAccessDetailForUploadedBucketObjects(List<BucketObjectEvent> uploadedBucketObjects) {
        log.info( "Inside createAccessDetailForUploadedBucketObjects" );
        return uploadedBucketObjects.stream()
                .map( this::createAccessDetailForBucketObject )
                .collect( Collectors.toList() );
    }
}