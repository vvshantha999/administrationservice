package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.ObjectAccessRequest;
import smartshare.administrationservice.dto.response.BucketObjectAccessRequestDto;
import smartshare.administrationservice.dto.response.ownertree.FolderComponent;
import smartshare.administrationservice.dto.response.usertree.UserFolderComponent;
import smartshare.administrationservice.service.BucketObjectAccessRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class ObjectAccessRequestController {

    private BucketObjectAccessRequestService bucketObjectAccessRequestService;

    @Autowired
    ObjectAccessRequestController(BucketObjectAccessRequestService bucketObjectAccessRequestService) {

        this.bucketObjectAccessRequestService = bucketObjectAccessRequestService;
    }

    @PostMapping(value = "/object/createAccessRequest")
    public ResponseEntity<Boolean> createObjectAccessRequest(@RequestBody List<ObjectAccessRequest> objectAccessRequestsFromUi) {
        log.info( "Inside createObjectAccessRequest" );
        return Boolean.TRUE.equals( bucketObjectAccessRequestService.createBucketObjectAccessRequests( objectAccessRequestsFromUi ) ) ? new ResponseEntity<>( true, HttpStatus.CREATED ) :
                new ResponseEntity<>( false, HttpStatus.BAD_REQUEST );
    }

    @DeleteMapping(value = "/object/deleteAccessRequest")
    public ResponseEntity<Boolean> deleteObjectAccessRequest(@RequestBody List<BucketObjectAccessRequestDto> objectAccessRequests) {
        log.info( "Inside createObjectAccessRequest" );
        return bucketObjectAccessRequestService.deleteBucketObjectAccessRequests( objectAccessRequests ) ? new ResponseEntity<>( true, HttpStatus.OK ) : new ResponseEntity<>( false, HttpStatus.BAD_REQUEST );
    }

    @PutMapping(value = "/object/approveAccessRequest")
    public ResponseEntity<Boolean> approveObjectAccessRequest(@RequestBody List<BucketObjectAccessRequestDto> objectAccessRequests) {
        log.info( "Inside approveObjectAccessRequest" );
        return bucketObjectAccessRequestService.approveBucketObjectAccessRequests( objectAccessRequests ) ?
                new ResponseEntity<>( true, HttpStatus.OK ) : new ResponseEntity<>( false, HttpStatus.BAD_REQUEST );
    }

    @PutMapping(value = "/object/rejectAccessRequest")
    public ResponseEntity<Boolean> rejectObjectAccessRequest(@RequestBody List<BucketObjectAccessRequestDto> objectAccessRequests) {
        log.info( "Inside rejectObjectAccessRequest" );
        return bucketObjectAccessRequestService.rejectBucketObjectAccessRequests( objectAccessRequests ) ? new ResponseEntity<>( true, HttpStatus.OK ) : new ResponseEntity<>( false, HttpStatus.BAD_REQUEST );
    }


    @GetMapping(value = "/accessRequestsCreatedByUser")
    public List<BucketObjectAccessRequestDto> getAccessRequestsCreatedByUser(@RequestParam("userId") int userId) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return bucketObjectAccessRequestService.getAccessRequestsCreatedByUser( userId );
    }

    @GetMapping(value = "/accessRequestsOfOwner")
    public List<BucketObjectAccessRequestDto> getAccessRequestsToBeApprovedByOwnerOfObject(@RequestParam("ownerId") int ownerId) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return bucketObjectAccessRequestService.getAccessRequestsToBeApprovedByOwner( ownerId );
    }

    // Relationship Screen

    @GetMapping(value = "/listOfUsersAccessingOwnersObject")
    public FolderComponent getListOfUsersAccessingOwnerObject(@RequestParam("bucketName") String bucketName, @RequestParam("ownerId") int ownerId) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        return bucketObjectAccessRequestService.getListOfUsersAccessingOwnerObjects( bucketName, ownerId );
    }

    @GetMapping(path = "userFiles")
    public UserFolderComponent getUserFilesByBucket(@RequestParam("bucketName") String bucketName, @RequestParam("userId") int userId) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        return bucketObjectAccessRequestService.getUserFilesByBucket( bucketName, userId );
    }

}
