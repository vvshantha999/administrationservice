package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.AccessingUsersInfoForApi;
import smartshare.administrationservice.dto.BucketObjectFromApi;
import smartshare.administrationservice.dto.ObjectMetadata;
import smartshare.administrationservice.dto.mappers.BucketObjectMapper;
import smartshare.administrationservice.models.BucketObject;
import smartshare.administrationservice.models.Status;
import smartshare.administrationservice.repository.BucketObjectRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class APIRequestService {

    private BucketObjectRepository bucketObjectRepository;
    private BucketObjectMapper bucketObjectMapper;
    private Status statusOfOperation;


    @Autowired
    APIRequestService(BucketObjectRepository bucketObjectRepository, Status statusOfOperation, BucketObjectMapper bucketObjectMapper) {
        this.bucketObjectRepository = bucketObjectRepository;
        this.statusOfOperation = statusOfOperation;
        this.bucketObjectMapper = bucketObjectMapper;
    }

    private Map<String, ObjectMetadata> dataFormatterForFetchMetaDataForObjectsInS3(BucketObject bucketObject) {
        log.info( "Inside dataFormatterForFetchMetaDataForObjectsInS3" );
        Map<String, ObjectMetadata> eachBucketObjectMap = new HashMap<>();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setOwnerName( bucketObject.getOwner().getUserName() );
        List<AccessingUsersInfoForApi> accessingUsersOfTheObject = bucketObject.getAccessingUsers().stream().map( accessingUser ->
                new AccessingUsersInfoForApi( accessingUser.getUser().getUserName(), accessingUser.getAccess() ) ).collect( Collectors.toList() );
        objectMetadata.setAccessingUsersInfo( accessingUsersOfTheObject );
        eachBucketObjectMap.put( bucketObject.getName(), objectMetadata );
        return eachBucketObjectMap;

    }

    public List<Map<String, ObjectMetadata>> fetchMetaDataForObjectsInS3() {
        log.info( "Inside fetchMetaDataForObjectsInS3 service layer" );
        List<BucketObject> allBucketObjects = this.bucketObjectRepository.findAll();
        List<Map<String, ObjectMetadata>> metadataOfAllBucketObjects = allBucketObjects.stream().map( this::dataFormatterForFetchMetaDataForObjectsInS3 ).collect( Collectors.toList() );
        System.out.println( "metadataOfAllBucketObjects----------->" + metadataOfAllBucketObjects );
        return metadataOfAllBucketObjects;

    }

    public Status deleteGivenObjectsInDb(List<String> objectNamesToBeDeleted) {
        log.info( "Inside deleteGivenObjectsInDb" );
        try {
            objectNamesToBeDeleted.forEach( this::deleteBucketObject );
            bucketObjectRepository.flush();
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );

        } catch (Exception e) {
            log.error( "Exception while deleting the object " + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }

    private void deleteBucketObject(String objectToBeDeleted) {
        bucketObjectRepository.delete( bucketObjectRepository.findByName( objectToBeDeleted ) );
    }

    public Status createAccessDetailForGivenBucketObject(List<BucketObjectFromApi> bucketObjectsFromApi) {
        log.info( "Inside createAccessDetailForGivenBucketObject" );
        try {
            List<BucketObject> bucketObjects = bucketObjectsFromApi.stream().map( bucketObjectFromApi -> (BucketObject) bucketObjectMapper.map( bucketObjectFromApi ) ).collect( Collectors.toList() );
            bucketObjectRepository.saveAll( bucketObjects );
            bucketObjectRepository.flush();
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
        } catch (Exception e) {
            log.error( "Exception while deleting the object " + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }
}
