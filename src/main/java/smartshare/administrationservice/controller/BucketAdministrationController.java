package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.RemoveUserFromBucket;
import smartshare.administrationservice.models.BucketAccessRequest;
import smartshare.administrationservice.models.Status;
import smartshare.administrationservice.service.BucketAccessRequestService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class BucketAdministrationController {

    private BucketAccessRequestService bucketAccessRequestService;


    @Autowired
    BucketAdministrationController(BucketAccessRequestService bucketAccessRequestService) {
        this.bucketAccessRequestService = bucketAccessRequestService;
    }

    @PostMapping(value = "/bucket/createAccessRequest")
    public ResponseEntity createBucketAccessRequest(@RequestBody BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        return bucketAccessRequestService.createBucketAccessRequest( bucketAccessRequestFromUi ) ?
                new ResponseEntity( HttpStatus.CREATED ) : new ResponseEntity( HttpStatus.BAD_REQUEST );
    }

    @PostMapping(value = "/bucket/approveAccessRequest")
    public ResponseEntity approveBucketAccessRequest(@RequestBody BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside createBucketAccessRequest" );
        return bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest ) ?
                new ResponseEntity( HttpStatus.CREATED ) : new ResponseEntity( HttpStatus.BAD_REQUEST );
    }

    @PutMapping(value = "/bucket/rejectAccessRequest")
    public ResponseEntity rejectBucketAccessRequest(@RequestBody BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside rejectBucketAccessRequest" );
        return bucketAccessRequestService.rejectBucketAccessRequest( bucketAccessRequest ) ?
                new ResponseEntity( HttpStatus.OK ) : new ResponseEntity( HttpStatus.BAD_REQUEST );
    }

    @PostMapping(value = "/bucket/addUser")
    public ResponseEntity addUserToBucket(@RequestBody AddUserFromUiToBucket addUserFromUiToBucket) {
        log.info( "Inside addUserToBucket" );
        return bucketAccessRequestService.addUserToBucketByBucketAdmin( addUserFromUiToBucket ) ?
                new ResponseEntity( HttpStatus.CREATED ) : new ResponseEntity( HttpStatus.BAD_REQUEST );
    }

    @DeleteMapping(value = "/bucket/removeUser")
    public ResponseEntity removeUserFromBucket(@RequestBody RemoveUserFromBucket removeUserFromBucket) {
        log.info( "Inside removeUserFromBucket" );
        Status removeUserFromBucketByBucketAdminResult = bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );
        return (removeUserFromBucketByBucketAdminResult.getValue()) ? new ResponseEntity( HttpStatus.OK ) :
                ResponseEntity.badRequest().body( removeUserFromBucketByBucketAdminResult.getReasonForFailure() );
    }

    @DeleteMapping(value = "/bucket/deleteAccessRequest")
    public ResponseEntity deleteBucketAccessRequest(@RequestBody BucketAccessRequest bucketAccessRequestTobeDeleted) {
        log.info( "Inside deleteBucketAccessRequest" );
        Status deleteBucketAccessRequestResult = bucketAccessRequestService.deleteBucketAccessRequest( bucketAccessRequestTobeDeleted );
        return (deleteBucketAccessRequestResult.getValue()) ? new ResponseEntity( HttpStatus.OK ) :
                (deleteBucketAccessRequestResult.getReasonForFailure().equals( HttpStatus.NOT_FOUND.getReasonPhrase() )) ?
                        ResponseEntity.notFound().build() :
                        new ResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR );
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



}
