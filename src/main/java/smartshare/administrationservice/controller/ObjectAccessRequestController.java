package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.dto.UsersAccessingOwnerObject;
import smartshare.administrationservice.models.BucketObjectAccessRequestEntity;
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
    public ResponseEntity createObjectAccessRequest(@RequestBody List<ObjectAccessRequestFromUi> objectAccessRequestsFromUi) {
        log.info( "Inside createObjectAccessRequest" );

        // scenario requesting access for folders or files either read or write. From Ui not sure how the input is

        return bucketObjectAccessRequestService.createBucketObjectAccessRequests( objectAccessRequestsFromUi ) ? new ResponseEntity( HttpStatus.CREATED ) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/object/deleteAccessRequest")
    public ResponseEntity deleteObjectAccessRequest(@RequestBody BucketObjectAccessRequestEntity objectAccessRequests) {
        log.info( "Inside createObjectAccessRequest" );
        return bucketObjectAccessRequestService.deleteBucketObjectAccessRequest( objectAccessRequests ) ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/object/approveAccessRequest")
    public ResponseEntity approveObjectAccessRequest(@RequestBody BucketObjectAccessRequestEntity objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );

        // in ui it has to send all the children files in folder to accept at a shot

        return bucketObjectAccessRequestService.approveBucketObjectAccessRequest( objectAccessRequest ) ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/object/rejectAccessRequest")
    public ResponseEntity rejectObjectAccessRequest(@RequestBody BucketObjectAccessRequestEntity objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        return bucketObjectAccessRequestService.rejectObjectAccessRequest( objectAccessRequest ) ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/listOfUsersAccessingOwnersObject")
    public List<UsersAccessingOwnerObject> getListOfUsersAccessingOwnerObject(@RequestParam("bucket") String bucketName, @RequestParam("owner") String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return bucketObjectAccessRequestService.getListOfUsersAccessingOwnerObjects( bucketName, ownerName );

    }
    @GetMapping(value = "/accessRequestsCreatedByUser")
    public List<BucketObjectAccessRequestEntity> getAccessRequestsCreatedByUser(@RequestParam("user") String userName) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return bucketObjectAccessRequestService.getAccessRequestsCreatedByUser( userName );
    }

    @GetMapping(value = "/accessRequestsOfOwner")
    public List<BucketObjectAccessRequestEntity> getAccessRequestsToBeApprovedByOwnerOfObject(@RequestParam("owner") String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return bucketObjectAccessRequestService.getAccessRequestsToBeApprovedByOwnerOfObject( ownerName );
    }


}
