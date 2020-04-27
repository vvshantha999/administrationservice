package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.dto.UserBucketMapping;
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


    @PostMapping(value = "/bucket/addUser")
    public ResponseEntity<Boolean> addUserToBucket(@RequestBody List<UserBucketMapping> addUserFromUiToBucket) {
        log.info( "Inside addUserToBucket" );
        return bucketAccessRequestService.addUsersToBucketByBucketAdmin( addUserFromUiToBucket ) ?
                new ResponseEntity<>( true, HttpStatus.CREATED ) : new ResponseEntity<>( false, HttpStatus.BAD_REQUEST );
    }

    @DeleteMapping(value = "/bucket/removeUser")
    public ResponseEntity removeUserFromBucket(@RequestBody UserBucketMapping removeUserFromBucket) {
        log.info( "Inside removeUserFromBucket" );
        Status removeUserFromBucketByBucketAdminResult = bucketAccessRequestService.removeUserFromBucketByBucketAdmin( removeUserFromBucket );
        return (removeUserFromBucketByBucketAdminResult.getValue()) ? new ResponseEntity( true, HttpStatus.OK ) :
                ResponseEntity.badRequest().body( removeUserFromBucketByBucketAdminResult.getReasonForFailure() );
    }





}
