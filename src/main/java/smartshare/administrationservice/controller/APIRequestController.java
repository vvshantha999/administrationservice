package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.BucketMetadata;
import smartshare.administrationservice.dto.BucketObjectMetadata;
import smartshare.administrationservice.service.APIRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class APIRequestController {

    private APIRequestService apiRequestService;

    @Autowired
    APIRequestController(APIRequestService apiRequestService) {
        this.apiRequestService = apiRequestService;
    }

    @GetMapping(value = "objects/accessInfo")
    public List<BucketObjectMetadata> fetchMetaDataForObjectsInGivenBucketForSpecificUser(@RequestParam("bucketName") String bucketName, @RequestParam("userName") String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return apiRequestService.fetchBucketObjectsMetaDataByBucketNameAndUserName( bucketName, userName );
    }

    @GetMapping(value = "buckets/accessInfo")
    public List<BucketMetadata> fetchMetaDataForBucketsInS3(@RequestParam("userName") String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return apiRequestService.fetchBucketsMetaDataByUserName( userName );
    }


}
