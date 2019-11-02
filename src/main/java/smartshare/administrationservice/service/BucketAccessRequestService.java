package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.RemoveUserFromBucket;
import smartshare.administrationservice.dto.mappers.AddUserFromUiToBucketMapper;
import smartshare.administrationservice.dto.mappers.BucketAccessRequestMapper;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAccessRequestRepository;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.UserRepository;

import java.util.List;


@Slf4j
@Service
public class BucketAccessRequestService {

    private BucketAccessRequestRepository bucketAccessRequestRepository;
    private BucketRepository bucketRepository;
    private UserRepository userRepository;
    private Status statusOfOperation;
    private BucketAccessRequestMapper bucketAccessRequestMapper;
    private AddUserFromUiToBucketMapper addUserFromUiToBucketMapper;


    @Autowired
    BucketAccessRequestService(BucketAccessRequestRepository bucketAccessRequestRepository,
                               Status statusOfOperation, BucketRepository bucketRepository,
                               UserRepository userRepository,
                               BucketAccessRequestMapper bucketAccessRequestMapper,
                               AddUserFromUiToBucketMapper addUserFromUiToBucketMapper) {
        this.bucketAccessRequestRepository = bucketAccessRequestRepository;
        this.statusOfOperation = statusOfOperation;
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
        this.bucketAccessRequestMapper = bucketAccessRequestMapper;
        this.addUserFromUiToBucketMapper = addUserFromUiToBucketMapper;
    }

    public Status createBucketAccessRequest(BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        try {
            BucketAccessRequest newBucketAccessRequest = bucketAccessRequestMapper.map( bucketAccessRequestFromUi );
            BucketAccessRequest createdBucketAccessRequest = bucketAccessRequestRepository.saveAndFlush( newBucketAccessRequest );
            System.out.println( "createdBucketAccessRequest------------->" + createdBucketAccessRequest );
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
        } catch (Exception e) {
            log.error( "Exception while creating bucket Access Request" + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }

    private Bucket addUserToBucket(Bucket bucketInWhichUserListToBeUpdated, UserBucketMapping userBucketMapping) {
        log.info( "Inside addUserToBucket" );
        bucketInWhichUserListToBeUpdated.addUser( userBucketMapping );
        return bucketRepository.saveAndFlush( bucketInWhichUserListToBeUpdated );

    }

    private UserBucketMapping isUserAddedInBucket(Bucket bucket, User user) {
        log.info( "Inside isUserAddedInBucket" );

        List<UserBucketMapping> usersInTheBucket = bucket.getAccessingUsers();
        return usersInTheBucket.stream()
                .filter( userBucketMapping -> userBucketMapping.getUser().equals( user ) )
                .findAny()
                .orElse( null );

    }

    private UserBucketMapping giveWritePermissionToTheUserInTheBucket(UserBucketMapping currentUserBucketMapping) {
        currentUserBucketMapping.getAccess().setWrite( Boolean.TRUE );
        return currentUserBucketMapping;

    }

    private void approveWriteAccessRequestHandler(BucketAccessRequest bucketAccessRequest) {
        UserBucketMapping userExistsInBucket = isUserAddedInBucket( bucketAccessRequest.getBucket(), bucketAccessRequest.getUser() );
        if (userExistsInBucket != null) {
            addUserToBucket( bucketAccessRequest.getBucket(), giveWritePermissionToTheUserInTheBucket( userExistsInBucket ) );
        } else {
            UserBucketMapping newUserBucketMappingWithWriteAccess = new UserBucketMapping( bucketAccessRequest.getUser(), bucketAccessRequest.getBucket(), new BucketAccess( Boolean.TRUE, Boolean.TRUE ) );
            addUserToBucket( bucketAccessRequest.getBucket(), newUserBucketMappingWithWriteAccess );
        }
    }

    private void createOrUpdateAccessRecordsBasedOnTheRequest(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside createOrUpdateAccessRecordsBasedOnTheRequest" );
        if (bucketAccessRequest.getAccess().getRead())
            addUserToBucket( bucketAccessRequest.getBucket(), new UserBucketMapping( bucketAccessRequest.getUser(), bucketAccessRequest.getBucket(), bucketAccessRequest.getAccess() ) );
        if (bucketAccessRequest.getAccess().getWrite()) approveWriteAccessRequestHandler( bucketAccessRequest );

    }

    public Status approveBucketAccessRequest(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside approveBucketAccessRequest" );
        try {
            bucketAccessRequest.approve();
            createOrUpdateAccessRecordsBasedOnTheRequest( bucketAccessRequest );
            bucketAccessRequestRepository.saveAndFlush( bucketAccessRequest );
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
        } catch (Exception e) {
            log.error( "Exception while approving the Bucket Access Request" + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }

    public Status rejectBucketAccessRequest(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside approveBucketAccessRequest" );
        try {
            bucketAccessRequest.reject();
            bucketAccessRequestRepository.saveAndFlush( bucketAccessRequest );
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
        } catch (Exception e) {
            log.error( "Exception while rejecting the Bucket Access Request" + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }


    public Status addUserToBucketByBucketAdmin(AddUserFromUiToBucket addUserFromUiToBucket) {
        log.info( "Inside addUserToBucket" );
        try {
            AddUserFromUiToBucketMapper mappedResult = addUserFromUiToBucketMapper.map( addUserFromUiToBucket );
            addUserToBucket( mappedResult.getBucketToWhichUserIsAdded(), mappedResult.getNewUserMapping() );
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
        } catch (Exception e) {
            log.error( "Exception while adding user to the Bucket by admin" + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }

    public Status removeUserFromBucketByBucketAdmin(RemoveUserFromBucket removeUserFromBucket) {
        log.info( "Inside removeUserToBucketByBucketAdmin" );
        try {
            Bucket bucketFromWhichUserIsRemoved = bucketRepository.findByName( removeUserFromBucket.getBucketName() );
            User user = userRepository.findByUserName( removeUserFromBucket.getUserName() );
            UserBucketMapping userBucketMapping = isUserAddedInBucket( bucketFromWhichUserIsRemoved, user );
            if (bucketFromWhichUserIsRemoved.getObjects() == null) {
                bucketFromWhichUserIsRemoved.removeUser( userBucketMapping );
                bucketRepository.saveAndFlush( bucketFromWhichUserIsRemoved );// not sure whether it works or not
                statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
            } else {
                statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
                statusOfOperation.setReasonForFailure( "The user has Objects which has to be deleted before removing the user" );
            }

        } catch (Exception e) {
            log.error( "Exception while removing user from the Bucket by admin" + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;
    }

    public List<BucketAccessRequest> getBucketAccessRequestForAdmin() {
        return bucketAccessRequestRepository.findAll();
    }

    public List<BucketAccessRequest> getBucketAccessRequestForUser(String userName) {
        return bucketAccessRequestRepository.findAllByUser( userRepository.findByUserName( userName ) );
    }

    public Status deleteBucketAccessRequest(BucketAccessRequest bucketAccessRequestTobeDeleted) {
        try {
            bucketAccessRequestRepository.delete( bucketAccessRequestTobeDeleted );
            statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
        } catch (Exception e) {
            log.error( "Exception while deleting the bucket access request by admin" + e.getMessage() );
            statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
        }
        return statusOfOperation;

    }
}
