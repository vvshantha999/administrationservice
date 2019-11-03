package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.BucketMetadata;
import smartshare.administrationservice.dto.ObjectMetadata;
import smartshare.administrationservice.service.APIRequestService;

import java.util.List;
import java.util.Map;

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
    public List<Map<String, ObjectMetadata>> fetchMetaDataForObjectsInGivenBucketForSpecificUser(@RequestParam("bucketName") String bucketName, @RequestParam("userName") String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return apiRequestService.fetchMetaDataForObjectsInGivenBucketForSpecificUser( bucketName, userName );
    }

    @GetMapping(value = "buckets/accessInfo")
    public List<Map<String, BucketMetadata>> fetchMetaDataForBucketsInS3(@RequestParam("userName") String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return apiRequestService.fetchMetaDataForBucketsInS3( userName );
    }


}
