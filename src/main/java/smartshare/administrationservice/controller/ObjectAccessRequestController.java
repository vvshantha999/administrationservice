package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.dto.UsersAccessingOwnerObject;
import smartshare.administrationservice.models.ObjectAccessRequest;
import smartshare.administrationservice.service.ObjectAccessRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class ObjectAccessRequestController {

    private ObjectAccessRequestService objectAccessRequestService;

    @Autowired
    ObjectAccessRequestController(ObjectAccessRequestService objectAccessRequestService) {
        this.objectAccessRequestService = objectAccessRequestService;
    }

    @PostMapping(value = "/object/createAccessRequest")
    public ResponseEntity createObjectAccessRequest(@RequestBody List<ObjectAccessRequestFromUi> objectAccessRequestsFromUi) {
        log.info( "Inside createObjectAccessRequest" );

        // scenario requesting access for folders or files either read or write. From Ui not sure how the input is

        return objectAccessRequestService.createObjectAccessRequest( objectAccessRequestsFromUi ) ? new ResponseEntity( HttpStatus.CREATED ) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/object/deleteAccessRequest")
    public ResponseEntity deleteObjectAccessRequest(@RequestBody List<ObjectAccessRequest> objectAccessRequests) {
        log.info( "Inside createObjectAccessRequest" );
        return objectAccessRequestService.deleteObjectAccessRequest( objectAccessRequests ) ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/object/approveAccessRequest")
    public ResponseEntity approveObjectAccessRequest(@RequestBody ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );

        // in ui it has to send all the children files in folder to accept at a shot

        return objectAccessRequestService.approveObjectAccessRequest( objectAccessRequest ) ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/object/rejectAccessRequest")
    public ResponseEntity rejectObjectAccessRequest(@RequestBody ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        return objectAccessRequestService.rejectObjectAccessRequest( objectAccessRequest ) ? ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/listOfUsersAccessingOwnersObject")
    public List<UsersAccessingOwnerObject> getListOfUsersAccessingOwnerObject(@RequestParam("owner") String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return objectAccessRequestService.getListOfUsersAccessingOwnerObject( ownerName );

    }

    @GetMapping(value = "/accessRequestsCreatedByUser")
    public List<ObjectAccessRequest> getAccessRequestsCreatedByUser(@RequestParam("user") String userName) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return objectAccessRequestService.getAccessRequestsCreatedByUser( userName );
    }

    @GetMapping(value = "/accessRequestsOfOwner")
    public List<ObjectAccessRequest> getAccessRequestsToBeApprovedByOwnerOfObject(@RequestParam("owner") String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnersFile" );
        return objectAccessRequestService.getAccessRequestsToBeApprovedByOwnerOfObject( ownerName );
    }


}
