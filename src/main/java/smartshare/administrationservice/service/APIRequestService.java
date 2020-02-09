package smartshare.administrationservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.AccessingUserInfoForApi;
import smartshare.administrationservice.dto.BucketMetadata;
import smartshare.administrationservice.dto.BucketObjectMetadata;
import smartshare.administrationservice.dto.ObjectMetadata;
import smartshare.administrationservice.dto.mappers.BucketObjectMapper;
import smartshare.administrationservice.models.Bucket;
import smartshare.administrationservice.models.BucketObject;
import smartshare.administrationservice.models.Status;
import smartshare.administrationservice.repository.BucketObjectRepository;
import smartshare.administrationservice.repository.BucketRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class APIRequestService {

    private BucketObjectRepository bucketObjectRepository;
    private BucketObjectMapper bucketObjectMapper;
    private Status statusOfOperation;
    private BucketRepository bucketRepository;
    private ObjectMapper jsonConverter;


    @Autowired
    APIRequestService(BucketObjectRepository bucketObjectRepository, Status statusOfOperation,
                      BucketObjectMapper bucketObjectMapper, BucketRepository bucketRepository,
                      ObjectMapper jsonConverter) {
        this.bucketObjectRepository = bucketObjectRepository;
        this.statusOfOperation = statusOfOperation;
        this.bucketObjectMapper = bucketObjectMapper;
        this.bucketRepository = bucketRepository;
        this.jsonConverter = jsonConverter;
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
        Bucket bucket = bucketRepository.findByName( bucketName );
        return bucket.getObjects().stream()
                .map( bucketObject -> dataFormatterForFetchMetaDataForObjectsInS3( bucketObject, userName ) )
                .collect( Collectors.toList() );
    }

    private Optional<BucketMetadata> extractBucketMetadata(Bucket bucket, String userName) {
        log.info( "Inside extractBucketMetadata" );
        return bucket.getAccessingUsers().stream()
                .filter( userBucketMapping -> userBucketMapping.getUser().getUserName().equals( userName ) )
                .findFirst()
                .map( userBucketMapping -> new BucketMetadata( bucket.getName(), userName, userBucketMapping.getAccess() ) );
    }

    public List<BucketMetadata> fetchMetaDataForBucketsInS3(String userName) {
        log.info( "Inside fetchMetaDataForBucketsInS3" );
        return bucketRepository.findAll().stream()
                .map( bucket -> extractBucketMetadata( bucket, userName ) )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .collect( Collectors.toList() );
    }


//    private Map<String, BucketMetadata> extractBucketMetadata(Bucket bucket, String userName) {
//        log.info( "Inside extractBucketMetadata" );
//        Map<String, BucketMetadata> eachBucketMap = new HashMap<>();
//        Optional<BucketMetadata> bucketMetadata = bucket.getAccessingUsers().stream()
//                .filter( userBucketMapping -> userBucketMapping.getUser().getUserName().equals( userName ) )
//                .findFirst()
//                .map( userBucketMapping -> new BucketMetadata( userBucketMapping.getUser().getUserName(), userBucketMapping.getAccess() ) );
//        eachBucketMap.put( bucket.getName(), bucketMetadata.orElse( null ) );
//        return eachBucketMap;
//    }
//
//    public List<Map<String, BucketMetadata>> fetchMetaDataForBucketsInS3(String userName) {
//        log.info( "Inside fetchMetaDataForBucketsInS3" );
//        return bucketRepository.findAll().stream()
//                .map( bucket -> extractBucketMetadata( bucket, userName ) )
//                .collect( Collectors.toList() );
//    }

//    private Map<String, ObjectMetadata> dataFormatterForFetchMetaDataForObjectsInS3(BucketObject bucketObject, String userName) {
//        log.info( "Inside dataFormatterForFetchMetaDataForObjectsInS3" );
//        Map<String, ObjectMetadata> eachBucketObjectMap = new HashMap<>();
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setOwnerName( bucketObject.getOwner().getUserName() );
//        bucketObject.getAccessingUsers().stream()
//                .filter( accessingUser -> accessingUser.getUser().getUserName().equals( userName ) )
//                .findFirst()
//                .ifPresent( accessingUser -> objectMetadata.setAccessingUserInfo( new AccessingUserInfoForApi( userName, accessingUser.getAccess() ) ) );
//        eachBucketObjectMap.put( bucketObject.getName(), objectMetadata );
//        return eachBucketObjectMap;
//
//    }

//    public List<Map<String, ObjectMetadata>> fetchMetaDataForObjectsInGivenBucketForSpecificUser(String bucketName, String userName) {
//        log.info( "Inside fetchMetaDataForObjectsInS3 service layer" );
//        Bucket bucket = bucketRepository.findByName( bucketName );
//        return bucket.getObjects().stream()
//                .map( bucketObject -> dataFormatterForFetchMetaDataForObjectsInS3( bucketObject, userName ) )
//                .collect( Collectors.toList() );
//    }

//    public Status deleteGivenObjectsInDb(List<BucketObjectFromApi> objectsToBeDeleted) {
//        log.info( "Inside deleteGivenObjectsInDb" );
//        try {
//            objectsToBeDeleted.forEach( bucketObjectFromApi -> deleteBucketObject( bucketObjectFromApi.getObjectName(), bucketObjectFromApi.getBucketName() ) );
//            bucketObjectRepository.flush();
//            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
//
//        } catch (Exception e) {
//            log.error( "Exception while deleting the object " + e.getMessage() );
//            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
//        }
//        return statusOfOperation;
//    }
//
//    private void deleteBucketObject(String objectToBeDeleted, String bucketName) {
//        bucketObjectRepository.delete( bucketObjectRepository.findByNameAndBucket_Name( objectToBeDeleted, bucketName ) );
//    }
//
//    public Status createAccessDetailForGivenBucketObject(List<BucketObjectFromApi> bucketObjectsFromApi) {
//        log.info( "Inside createAccessDetailForGivenBucketObject" );
//        try {
//            List<BucketObject> bucketObjects = bucketObjectsFromApi.stream()
//                    .map( bucketObjectFromApi -> (BucketObject) bucketObjectMapper.map( bucketObjectFromApi ) )
//                    .collect( Collectors.toList() );
//            bucketObjectRepository.saveAll( bucketObjects );
//            bucketObjectRepository.flush();
//            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
//        } catch (Exception e) {
//            log.error( "Exception while deleting the object " + e.getMessage() );
//            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
//        }
//        return statusOfOperation;
//    }

//    @KafkaListener(groupId = "accessManagementObjectConsumer", topics = "AccessManagement")
//    public void consume(String bucketObject, ConsumerRecord record) {
//
//        try {
//            Status status;
//            switch (record.key().toString()) {
//                case "emptyBucketObject":
//                    log.info( "Consumed emptyBucketObject Event" );
//                    List<BucketObjectFromApi> bucketObjectEvent = jsonConverter.readValue( bucketObject, List.class );
//                    status = this.createAccessDetailForGivenBucketObject( bucketObjectEvent );
//                    log.info( "Status of emptyBucketObject Event in Access Management Server " + status.getMessage() );
//                    break;
//                case "uploadBucketObjects":
//                    log.info( "Consumed uploadBucketObjects Event" );
//                    List<BucketObjectFromApi> bucketObjectEvents = jsonConverter.readValue( bucketObject, List.class );
//                    status = this.createAccessDetailForGivenBucketObject( bucketObjectEvents );
//                    log.info( "Status of uploadBucketObjects Event in Access Management Server " + status.getMessage() );
//                    break;
//                case "deleteBucketObjects":
//                    log.info( "Consumed deleteBucketObjects Event" );
//                    List<BucketObjectFromApi> bucketObjectDeleteEvents = jsonConverter.readValue( bucketObject, List.class );
//                    status = this.deleteGivenObjectsInDb( bucketObjectDeleteEvents );
//                    log.info( "Status of deleteBucketObjects Event in Access Management Server " + status.getMessage() );
//                default:
//                    log.info( "Inside default event" );
//            }
//        } catch (Exception e) {
//            log.error( "Exception while handling the accessManagementBucketConsumer events " + e.getMessage() );
//        }
//    }


}
