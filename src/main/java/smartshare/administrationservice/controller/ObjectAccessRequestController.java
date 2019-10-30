package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.models.ObjectAccessRequest;
import smartshare.administrationservice.models.Status;
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
    public Status createObjectAccessRequest(@RequestBody List<ObjectAccessRequest> objectAccessRequests) {
        log.info( "Inside createObjectAccessRequest" );
        return objectAccessRequestService.createObjectAccessRequest( objectAccessRequests );
    }

    @DeleteMapping(value = "/object/deleteAccessRequest")
    public Status deleteObjectAccessRequest(@RequestBody List<ObjectAccessRequest> objectAccessRequests) {
        log.info( "Inside createObjectAccessRequest" );
        return objectAccessRequestService.deleteObjectAccessRequest( objectAccessRequests );
    }

    @PutMapping(value = "/object/approveAccessRequest")
    public Status approveObjectAccessRequest(@RequestBody ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        return objectAccessRequestService.approveObjectAccessRequest( objectAccessRequest );
    }

    @PutMapping(value = "/object/rejectAccessRequest")
    public Status rejectObjectAccessRequest(@RequestBody ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        return objectAccessRequestService.rejectObjectAccessRequest( objectAccessRequest );
    }


}
