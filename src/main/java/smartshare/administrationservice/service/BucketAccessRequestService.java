package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.RemoveUserFromBucket;
import smartshare.administrationservice.dto.mappers.AddUserFromUiToBucketMapper;
import smartshare.administrationservice.dto.mappers.BucketAccessRequestMapper;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAccessRequestRepository;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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


    @Transactional
    public Boolean createBucketAccessRequest(BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        try {
            BucketAccessRequest newBucketAccessRequest = bucketAccessRequestMapper.map( bucketAccessRequestFromUi );
            BucketAccessRequest createdBucketAccessRequest = bucketAccessRequestRepository.save( newBucketAccessRequest );
            System.out.println( "createdBucketAccessRequest------------->" + createdBucketAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while creating bucket Access Request" + e );
        }
        return Boolean.FALSE;
    }

    private Bucket addUserToBucket(Bucket bucketInWhichUserListToBeUpdated, UserBucketMapping userBucketMapping) {
        log.info( "Inside addUserToBucket" );
        if ((bucketInWhichUserListToBeUpdated.getAccessingUsers().isEmpty())) {
            bucketInWhichUserListToBeUpdated.setAccessingUsers( Collections.singletonList( userBucketMapping ) );
        } else {
            bucketInWhichUserListToBeUpdated.addUser( userBucketMapping );
        }
        return bucketRepository.save( bucketInWhichUserListToBeUpdated );
    }

    private Optional<UserBucketMapping> isUserAddedInBucket(Bucket bucket, User user) {
        log.info( "Inside isUserAddedInBucket" );

        List<UserBucketMapping> usersInTheBucket = bucket.getAccessingUsers();
        return !usersInTheBucket.isEmpty() ?
                usersInTheBucket.stream()
                        .filter( userBucketMapping -> userBucketMapping.getUser().getUserName() == user.getUserName() )
                        .findAny() : Optional.empty();

    }

    private UserBucketMapping giveWritePermissionToTheUserInTheBucket(UserBucketMapping currentUserBucketMapping) {
        currentUserBucketMapping.getAccess().setWrite( Boolean.TRUE );
        return currentUserBucketMapping;

    }

    private void approveWriteAccessRequestHandler(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside approveWriteAccessRequestHandler" );
        Optional<UserBucketMapping> userExistsInBucket = isUserAddedInBucket( bucketAccessRequest.getBucket(), bucketAccessRequest.getUser() );
        if (userExistsInBucket.isPresent()) {
            addUserToBucket( bucketAccessRequest.getBucket(), giveWritePermissionToTheUserInTheBucket( userExistsInBucket.get() ) );
        } else {
            UserBucketMapping newUserBucketMappingWithWriteAccess = new UserBucketMapping(
                    bucketAccessRequest.getUser(), bucketAccessRequest.getBucket(), new BucketAccess( Boolean.TRUE, Boolean.TRUE ) );
            addUserToBucket( bucketAccessRequest.getBucket(), newUserBucketMappingWithWriteAccess );
        }
    }

    private void createOrUpdateAccessRecordsBasedOnTheRequest(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside createOrUpdateAccessRecordsBasedOnTheRequest" );
        if (bucketAccessRequest.getAccess().getRead())
            addUserToBucket(
                    bucketAccessRequest.getBucket(),
                    new UserBucketMapping(
                            bucketAccessRequest.getUser(),
                            bucketAccessRequest.getBucket(),
                            bucketAccessRequest.getAccess() ) );
        if (bucketAccessRequest.getAccess().getWrite()) approveWriteAccessRequestHandler( bucketAccessRequest );

    }

    public Boolean approveBucketAccessRequest(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside approveBucketAccessRequest" );
        try {
            bucketAccessRequest.approve();
            createOrUpdateAccessRecordsBasedOnTheRequest( bucketAccessRequest );
            bucketAccessRequestRepository.saveAndFlush( bucketAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while approving the Bucket Access Request" + e.getMessage() );
        }
        return Boolean.FALSE;
    }

    public Boolean rejectBucketAccessRequest(BucketAccessRequest bucketAccessRequest) {
        log.info( "Inside rejectBucketAccessRequest" );
        try {
            bucketAccessRequest.reject();
            bucketAccessRequestRepository.save( bucketAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while rejecting the Bucket Access Request" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public Boolean addUserToBucketByBucketAdmin(AddUserFromUiToBucket addUserFromUiToBucket) {
        log.info( "Inside addUserToBucketByBucketAdmin" );
        try {
            Bucket mappedBucket = addUserFromUiToBucketMapper.map( addUserFromUiToBucket );
            bucketRepository.save( mappedBucket );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while adding user to the Bucket by admin" + e.getMessage() );
        }
        return Boolean.FALSE;
    }

    private Boolean doesUserHaveOnlyUserFolderObject(List<BucketObject> bucketObjects, String userName) {
        List<BucketObject> temp = (bucketObjects.stream()
                .filter( bucketObject -> bucketObject.getName().contains( userName + "/" ) )
                .collect( Collectors.toList() ));
        temp.forEach( System.out::println );
        System.out.println( bucketObjects.size() );
        return temp.size() == 1 ? Boolean.TRUE : Boolean.FALSE;
    }


    public Status removeUserFromBucketByBucketAdmin(RemoveUserFromBucket removeUserFromBucket) {
        log.info( "Inside removeUserToBucketByBucketAdmin" );
        try {
            Bucket bucketFromWhichUserIsRemoved = bucketRepository.findByName( removeUserFromBucket.getBucketName() );
            User user = userRepository.findByUserName( removeUserFromBucket.getUserName() );
            if (null != bucketFromWhichUserIsRemoved && null != user) {
                Optional<UserBucketMapping> doesUserBucketMappingExists = isUserAddedInBucket( bucketFromWhichUserIsRemoved, user );
                Boolean checkExistence = doesUserHaveOnlyUserFolderObject( bucketFromWhichUserIsRemoved.getObjects(), removeUserFromBucket.getUserName() );
                System.out.println( "checkExistence------->" + checkExistence );
                if (doesUserBucketMappingExists.isPresent() && checkExistence) {
                    bucketFromWhichUserIsRemoved.removeUser( doesUserBucketMappingExists.get() );
                    bucketRepository.save( bucketFromWhichUserIsRemoved );
                    statusOfOperation.setValue( Boolean.TRUE );
                } else {
                    statusOfOperation.setValue( Boolean.FALSE );
                statusOfOperation.setReasonForFailure( "The user has Objects which has to be deleted before removing the user" );
            }
            }
        } catch (Exception e) {
            log.error( "Exception while removing user from the Bucket by admin" + e );
            statusOfOperation.setValue( Boolean.FALSE );
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
            Optional<BucketAccessRequest> doesBucketAccessRequestWhichWillBeDeletedExists = bucketAccessRequestRepository.findById( bucketAccessRequestTobeDeleted.getId() );
            if (doesBucketAccessRequestWhichWillBeDeletedExists.isPresent()) {
                bucketAccessRequestRepository.delete( bucketAccessRequestTobeDeleted );
                statusOfOperation.setValue( Boolean.TRUE );
            } else {
                statusOfOperation.setValue( Boolean.FALSE );
                statusOfOperation.setReasonForFailure( HttpStatus.NOT_FOUND.getReasonPhrase() );
            }
        } catch (Exception e) {
            log.error( "Exception while deleting the bucket access request by admin" + e.getMessage() );
            statusOfOperation.setValue( Boolean.FALSE );
            statusOfOperation.setReasonForFailure( HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() );
        }
        return statusOfOperation;

    }
}
