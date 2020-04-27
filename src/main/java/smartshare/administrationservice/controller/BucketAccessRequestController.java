package smartshare.administrationservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.dto.response.BucketAccessRequestDto;
import smartshare.administrationservice.models.BucketAccessRequestEntity;
import smartshare.administrationservice.service.BucketAccessRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class BucketAccessRequestController {

    private final BucketAccessRequestService bucketAccessRequestService;

    @Autowired
    public BucketAccessRequestController(BucketAccessRequestService bucketAccessRequestService) {
        this.bucketAccessRequestService = bucketAccessRequestService;
    }

    @GetMapping(value = "/bucket/bucketAccessRequestsForAdmin")
    public List<BucketAccessRequestDto> getBucketAccessRequests() {
        log.info( "Inside getBucketAccessRequests" );
        return bucketAccessRequestService.getBucketAccessRequestForAdmin();
    }

    @GetMapping(value = "/bucket/bucketAccessRequestForUser")
    public List<BucketAccessRequestEntity> getBucketAccessRequestForUser(@RequestParam("userName") String userName) {
        log.info( "Inside getBucketAccessRequestForUser" );
        return bucketAccessRequestService.getBucketAccessRequestForUser( userName );
    }

    @PostMapping(value = "/bucket/createAccessRequest")
    public ResponseEntity createBucketAccessRequest(@RequestBody BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        return Boolean.TRUE.equals( bucketAccessRequestService.createBucketAccessRequest( bucketAccessRequestFromUi ) ) ?
                new ResponseEntity<>( true, HttpStatus.CREATED ) : new ResponseEntity<>( false, HttpStatus.BAD_REQUEST );
    }

    @PostMapping(value = "/bucket/approveAccessRequest")
    public ResponseEntity<HttpStatus> approveBucketAccessRequest(@RequestBody BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside createBucketAccessRequest" );
        return Boolean.TRUE.equals( bucketAccessRequestService.approveBucketAccessRequest( bucketAccessRequest ) ) ?
                new ResponseEntity<>( HttpStatus.CREATED ) : new ResponseEntity<>( HttpStatus.BAD_REQUEST );
    }

    @PutMapping(value = "/bucket/rejectAccessRequest")
    public ResponseEntity<HttpStatus> rejectBucketAccessRequest(@RequestBody BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside rejectBucketAccessRequest" );
        return Boolean.TRUE.equals( bucketAccessRequestService.rejectBucketAccessRequest( bucketAccessRequest ) ) ?
                new ResponseEntity<>( HttpStatus.OK ) : new ResponseEntity<>( HttpStatus.BAD_REQUEST );
    }

    @DeleteMapping(value = "/bucket/deleteAccessRequest")
    public ResponseEntity<HttpStatus> deleteBucketAccessRequest(@RequestBody BucketAccessRequestEntity bucketAccessRequestTobeDeleted) {
        log.info( "Inside deleteBucketAccessRequest" );
        Status deleteBucketAccessRequestResult = bucketAccessRequestService.deleteBucketAccessRequest( bucketAccessRequestTobeDeleted );
        if (Boolean.TRUE.equals( deleteBucketAccessRequestResult.getValue() )) {
            return new ResponseEntity<>( HttpStatus.OK );
        }
        return (deleteBucketAccessRequestResult.getReasonForFailure().equals( HttpStatus.NOT_FOUND.getReasonPhrase() )) ?
                ResponseEntity.notFound().build() :
                new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );

    }

}
