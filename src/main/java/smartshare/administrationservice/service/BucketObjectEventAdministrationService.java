package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.BucketObjectFromApi;
import smartshare.administrationservice.dto.mappers.BucketObjectMapper;
import smartshare.administrationservice.models.BucketObject;
import smartshare.administrationservice.repository.BucketObjectRepository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BucketObjectEventAdministrationService {

    private BucketObjectRepository bucketObjectRepository;
    private BucketObjectMapper bucketObjectMapper;


    @Autowired
    BucketObjectEventAdministrationService(BucketObjectRepository bucketObjectRepository,
                                           BucketObjectMapper bucketObjectMapper) {
        this.bucketObjectRepository = bucketObjectRepository;
        this.bucketObjectMapper = bucketObjectMapper;

    }


    private void deleteBucketObject(BucketObjectFromApi bucketObjectFromApi) {
        bucketObjectRepository.delete( bucketObjectRepository.findByNameAndBucket_Name( bucketObjectFromApi.getObjectName(), bucketObjectFromApi.getBucketName() ) );
        log.info( bucketObjectFromApi.getObjectName() + " access details deleted successfully" );
    }

    public void deleteBucketObjects(List<BucketObjectFromApi> objectsToBeDeleted) {
        log.info( "Inside deleteGivenObjectsInDb" );
        try {
            objectsToBeDeleted.forEach( this::deleteBucketObject );
        } catch (Exception e) {
            log.error( "Exception while deleting the object " + objectsToBeDeleted + " " + e );
        }
    }


    public void createAccessDetailForBucketObject(BucketObjectFromApi bucketObjectFromApi) {
        log.info( "Inside createAccessDetailForBucketObject" );
        try {
            BucketObject bucketObject = bucketObjectMapper.map( bucketObjectFromApi );
            bucketObjectRepository.save( bucketObject );
            log.info( bucketObject.toString() + " access details created successfully" );
        } catch (Exception e) {
            log.error( "Exception while createAccessDetailForGivenBucketObject " + e );
        }
    }

    public void createAccessDetailForUploadedBucketObjects(List<BucketObjectFromApi> uploadedBucketObjects) {
        log.info( "Inside createAccessDetailForUploadedBucketObjects" );
        uploadedBucketObjects.forEach( this::createAccessDetailForBucketObject );
    }


    @KafkaListener(groupId = "accessManagementObjectConsumer", topics = "AccessManagement")
    public void consume(BucketObjectFromApi[] bucketObjects, ConsumerRecord record) {

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
