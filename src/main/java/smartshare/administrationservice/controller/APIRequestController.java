package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.UserDto;
import smartshare.administrationservice.dto.response.BucketObjectsMetadata;
import smartshare.administrationservice.dto.response.BucketsMetadata;
import smartshare.administrationservice.dto.response.UserLoginStatus;
import smartshare.administrationservice.dto.response.UserMetadata;
import smartshare.administrationservice.models.UserAggregate;
import smartshare.administrationservice.service.APIRequestService;

import java.util.List;

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
    public BucketObjectsMetadata fetchMetaDataForObjectsInGivenBucketForSpecificUser(@RequestParam("bucketName") String bucketName, @RequestParam("userId") int userId) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return new BucketObjectsMetadata( apiRequestService.fetchBucketObjectsMetaDataByBucketNameAndUserId( bucketName, userId ) );
    }

    @GetMapping(value = "buckets/accessInfo")
    public BucketsMetadata fetchMetaDataForBucketsInS3(@RequestParam("userName") String userName, @RequestParam("email") String email) {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return new BucketsMetadata( apiRequestService.fetchBucketsMetaDataByUserName( userName, email ) );
    }

    @PostMapping(path = "register")
    public UserLoginStatus registerUser(@RequestBody UserDto user) {
        log.info( "Inside register user" );
        System.out.println( user );
        final UserLoginStatus userLoginStatus = apiRequestService.registerUserAndCheckIsAdmin( user );
        System.out.println( "userLoginStatus----->" + userLoginStatus );
        return userLoginStatus;
    }

    @GetMapping(path = "users")
    public List<UserAggregate> getUsers() {
        log.info( "Inside register user" );
        return apiRequestService.getUsers();
    }

    @GetMapping(path = "usersMetadata")
    public List<UserMetadata> getUsersMetadata() {
        log.info( "Inside getUsersMetadata" );
        return apiRequestService.getUsersMetadata();
    }

    @PostMapping("makeAdmin")
    public Boolean makeUserAdmin(@RequestBody UserDto user) {
        log.info( "Inside makeUserAdmin" );
        return apiRequestService.createAdmin( user );
    }


}
