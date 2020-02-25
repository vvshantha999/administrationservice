package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.BucketObjectEvent;
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.models.UserAggregate;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BucketObjectEventAdministrationService {

    private BucketAggregateRepository bucketAggregateRepository;
    private UserAggregateRepository userAggregateRepository;


    @Autowired
    BucketObjectEventAdministrationService(

            BucketAggregateRepository bucketAggregateRepository,
            UserAggregateRepository userAggregateRepository
    ) {
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.userAggregateRepository = userAggregateRepository;
    }


    private void deleteBucketObject(BucketObjectEvent bucketObjectFromApi) {
        BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketObjectFromApi.getBucketName() ) );
        UserAggregate owner = Objects.requireNonNull( userAggregateRepository.findByUserName( bucketObjectFromApi.getOwnerName() ) );
        if (bucket.removeBucketObject( bucketObjectFromApi.getObjectName(), owner.getUserId() )) {
            bucketAggregateRepository.save( bucket );
            log.info( bucketObjectFromApi.getObjectName() + " access details deleted successfully" );
        }
        log.info( bucketObjectFromApi.getObjectName() + " access details deletion  failed as the bucket object was not found" );
    }

    public void deleteBucketObjects(List<BucketObjectEvent> objectsToBeDeleted) {
        log.info( "Inside deleteGivenObjectsInDb" );
        try {
            objectsToBeDeleted.forEach( this::deleteBucketObject );
        } catch (Exception e) {
            log.error( "Exception while deleting the object " + objectsToBeDeleted + " " + e );
        }
    }


    public void createAccessDetailForBucketObject(BucketObjectEvent bucketObjectFromApi) {
        log.info( "Inside createAccessDetailForBucketObject" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketObjectFromApi.getBucketName() ) );
            UserAggregate owner = Objects.requireNonNull( userAggregateRepository.findByUserName( bucketObjectFromApi.getOwnerName() ) );
            if (bucket.isUserExistsInBucket( owner.getUserId() )) {
                System.out.println( "inside" );
                bucket.addBucketObject( bucketObjectFromApi.getObjectName(), owner.getUserId() );
                bucketAggregateRepository.save( bucket );
                log.info( bucketObjectFromApi.toString() + " access details created successfully" );
            } else
                log.error( "User has not Authorized to create Bucket Object in this bucket" );

        } catch (Exception e) {
            log.error( "Exception while createAccessDetailForGivenBucketObject " + e );
        }
    }

    public void createAccessDetailForUploadedBucketObjects(List<BucketObjectEvent> uploadedBucketObjects) {
        log.info( "Inside createAccessDetailForUploadedBucketObjects" );
        uploadedBucketObjects.forEach( this::createAccessDetailForBucketObject );
    }


    //    @KafkaListener(groupId = "accessManagementObjectConsumer", topics = "AccessManagement")
    public void consume(BucketObjectEvent[] bucketObjects, ConsumerRecord record) {

        try {

            switch (record.key().toString()) {
                case "emptyBucketObject":
                    log.info( "Consumed emptyBucketObject Event" );
                    this.createAccessDetailForBucketObject( bucketObjects[0] );
                    break;
                case "uploadBucketObjects":
                    log.info( "Consumed uploadBucketObjects Event" );
                    this.createAccessDetailForUploadedBucketObjects( Arrays.asList( bucketObjects ) );
                    break;
                case "deleteBucketObjects":
                    log.info( "Consumed deleteBucketObjects Event" );
                    this.deleteBucketObjects( Arrays.asList( bucketObjects ) );
                    break;
                default:
                    log.error( "Unsupported accessManagementBucketConsumer event" );
            }
        } catch (Exception e) {
            log.error( "Exception while handling the accessManagementBucketConsumer events " + e.getMessage() );
        }
    }


}
