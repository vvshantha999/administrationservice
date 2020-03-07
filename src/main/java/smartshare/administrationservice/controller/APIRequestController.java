package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.response.BucketObjectsMetadata;
import smartshare.administrationservice.dto.response.BucketsMetadata;
import smartshare.administrationservice.service.APIRequestService;

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
    public BucketObjectsMetadata fetchMetaDataForObjectsInGivenBucketForSpecificUser(@RequestParam("bucketName") String bucketName, @RequestParam("userName") String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return new BucketObjectsMetadata( apiRequestService.fetchBucketObjectsMetaDataByBucketNameAndUserName( bucketName, userName ) );
    }

    @GetMapping(value = "buckets/accessInfo")
    public BucketsMetadata fetchMetaDataForBucketsInS3(@RequestParam("userName") String userName) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return new BucketsMetadata( apiRequestService.fetchBucketsMetaDataByUserName( userName ) );
    }


}
