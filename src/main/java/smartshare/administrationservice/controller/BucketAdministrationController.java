package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.RemoveUserFromBucket;
import smartshare.administrationservice.models.BucketAccessRequest;
import smartshare.administrationservice.models.Status;
import smartshare.administrationservice.service.BucketAccessRequestService;
import smartshare.administrationservice.service.BucketAdministrationService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class BucketAdministrationController {

    private BucketAdministrationService bucketAdministrationService;
    private BucketAccessRequestService bucketAccessRequestService;

    @Autowired
    BucketAdministrationController(BucketAdministrationService bucketAdministrationService, BucketAccessRequestService bucketAccessRequestService) {
        this.bucketAdministrationService = bucketAdministrationService;
        this.bucketAccessRequestService = bucketAccessRequestService;
    }

    @PostMapping(value = "/bucket/createAccessRequest")
    public Status createBucketAccessRequest(@RequestBody BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        return bucketAccessRequestService.createBucketAccessRequest( bucketAccessRequestFromUi );
    }

    @PostMapping(value = "/bucket/approveAccessRequest")
    public Status approveBucketAccessRequest(@RequestBody BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside createBucketAccessRequest" );
        return bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest );
    }

    @PostMapping(value = "/bucket/rejectAccessRequest")
    public Status rejectBucketAccessRequest(@RequestBody BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside rejectBucketAccessRequest" );
        return bucketAccessRequestService.rejectBucketAccessRequest( bucketAccessRequest );
    }

    @PostMapping(value = "/bucket/addUser")
    public Status addUserToBucket(@RequestBody AddUserFromUiToBucket addUserFromUiToBucket) {
        log.info( "Inside addUserToBucket" );
        return bucketAccessRequestService.addUserToBucketByBucketAdmin( addUserFromUiToBucket );
    }

    @DeleteMapping(value = "/bucket/removeUser")
    public Status removeUserFromBucket(@RequestBody RemoveUserFromBucket removeUserFromBucket) {
        log.info( "Inside removeUserFromBucket" );
        return bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );
    }

    @GetMapping(value = "/buckets/accessRequests")
    public List<BucketAccessRequest> getBucketAccessRequestForAdmin() {
        log.info( "Inside getBucketAccessRequestForAdmin" );
        return bucketAccessRequestService.getBucketAccessRequestForAdmin();
    }

    @GetMapping(value = "/bucket/bucketAccessRequestForUser")
    public List<BucketAccessRequest> getBucketAccessRequestForUser(@RequestParam("userName") String userName) {
        log.info( "Inside getBucketAccessRequestForUser" );
        return bucketAccessRequestService.getBucketAccessRequestForUser( userName );
    }

    @DeleteMapping(value = "/bucket/deleteAccessRequest")
    public Status deleteBucketAccessRequest(@RequestBody BucketAccessRequest bucketAccessRequestTobeDeleted) {
        log.info( "Inside deleteBucketAccessRequest" );
        return bucketAccessRequestService.deleteBucketAccessRequest( bucketAccessRequestTobeDeleted );
    }


}
