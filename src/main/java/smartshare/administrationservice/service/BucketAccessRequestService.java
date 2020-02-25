package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.dto.UserBucketMapping;
import smartshare.administrationservice.dto.mappers.BucketAccessRequestMapper;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAccessEntityRepository;
import smartshare.administrationservice.repository.BucketAccessRequestEntityRepository;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class BucketAccessRequestService {

    private BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository;
    private BucketAccessEntityRepository bucketAccessEntityRepository;
    private BucketAggregateRepository bucketAggregateRepository;
    private UserAggregateRepository userAggregateRepository;
    private BucketAccessRequestMapper bucketAccessRequestMapper;
    private Status status;

    @Autowired
    BucketAccessRequestService(
            BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository,
            Status status,
            BucketAccessEntityRepository bucketAccessEntityRepository,
            BucketAggregateRepository bucketAggregateRepository,
            UserAggregateRepository userAggregateRepository,
            BucketAccessRequestMapper bucketAccessRequestMapper) {

        this.bucketAccessRequestEntityRepository = bucketAccessRequestEntityRepository;
        this.status = status;
        this.bucketAccessEntityRepository = bucketAccessEntityRepository;
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.userAggregateRepository = userAggregateRepository;
        this.bucketAccessRequestMapper = bucketAccessRequestMapper;
    }


    @Transactional
    public Boolean createBucketAccessRequest(BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        try {
            BucketAccessRequestEntity newBucketAccessRequest = bucketAccessRequestMapper.map( bucketAccessRequestFromUi );
            BucketAccessRequestEntity createdBucketAccessRequest = bucketAccessRequestEntityRepository.save( newBucketAccessRequest );
            System.out.println( "createdBucketAccessRequest------------->" + createdBucketAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while creating bucket Access Request" + e );
        }
        return Boolean.FALSE;
    }

    public Status deleteBucketAccessRequest(BucketAccessRequestEntity bucketAccessRequestTobeDeleted) {
        try {
            Optional<BucketAccessRequestEntity> doesBucketAccessRequestWhichWillBeDeletedExists = bucketAccessRequestEntityRepository.findById( bucketAccessRequestTobeDeleted.getId() );
            if (doesBucketAccessRequestWhichWillBeDeletedExists.isPresent()) {
                bucketAccessRequestEntityRepository.delete( bucketAccessRequestTobeDeleted );
                status.setValue( Boolean.TRUE );
            } else {
                status.setValue( Boolean.FALSE );
                status.setReasonForFailure( HttpStatus.NOT_FOUND.getReasonPhrase() );
            }
        } catch (Exception e) {
            log.error( "Exception while deleting the bucket access request by admin" + e.getMessage() );
            status.setValue( Boolean.FALSE );
            status.setReasonForFailure( HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() );
        }
        return status;

    }

    private Boolean approveReadBucketAccessRequest(BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside approveReadBucketAccessRequest" );
        try {
            Optional<BucketAggregate> bucket = bucketAggregateRepository.findById( bucketAccessRequest.getBucketId() );
            bucket.ifPresent( bucketAggregate -> {
                bucketAggregate.addBucketAccessingUsers( bucketAccessRequest.getUserId(), bucketAccessRequest.getBucketAccessId() );
                bucketAggregateRepository.save( bucketAggregate );
            } );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while adding user To Bucket" + e );
        }
        return Boolean.FALSE;
    }

    private Boolean approveWriteBucketAccessRequest(BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside approveWriteBucketAccessRequest" );
        try {
            Optional<BucketAggregate> bucket = bucketAggregateRepository.findById( bucketAccessRequest.getBucketId() );
            Optional<UserAggregate> user = userAggregateRepository.findById( bucketAccessRequest.getUserId() );
            BucketAccessEntity readAndWriteAccess = bucketAccessEntityRepository.findByReadAndWrite( true, true );

            if (bucket.isPresent() && user.isPresent() && null != readAndWriteAccess) {

                boolean isRecordExists = false;
                BucketAggregate bucketAggregate = bucket.get();

                for (BucketAccessingUser bucketAccessingUser : bucketAggregate.getBucketAccessingUsers()) {
                    if (bucketAccessingUser.getUserId() == bucketAccessRequest.getUserId()) {
                        isRecordExists = true;
                        bucketAccessingUser.setBucketAccessId( readAndWriteAccess.getBucketAccessId() );
                    }
                }
                if (!isRecordExists)
                    bucketAggregate.addBucketAccessingUsers( bucketAccessRequest.getUserId(), readAndWriteAccess.getBucketAccessId() );
                // create bucketObject
                bucketAggregate.addBucketObject( user.get().getUserName() + "/", user.get().getUserId() );
                bucketAggregateRepository.save( bucketAggregate );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Exception while adding user To Bucket" + e );
        }
        return Boolean.FALSE;
    }


    private Boolean approveByRequestType(BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside updateOrInsertAccess" );
        Optional<BucketAccessEntity> accessObject = bucketAccessEntityRepository.findById( bucketAccessRequest.getBucketAccessId() );
        if (accessObject.isPresent() && accessObject.get().getRead())
            return approveReadBucketAccessRequest( bucketAccessRequest );
        return approveWriteBucketAccessRequest( bucketAccessRequest );
    }


    @Transactional
    public Boolean approveBucketAccessRequest(BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside approveBucketAccessRequest" );
        try {
            if (approveByRequestType( bucketAccessRequest.approve() )) {
                bucketAccessRequestEntityRepository.save( bucketAccessRequest );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Exception while approving the Bucket Access Request" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public Boolean rejectBucketAccessRequest(BucketAccessRequestEntity bucketAccessRequest) {
        log.info( "Inside rejectBucketAccessRequest" );
        try {
            bucketAccessRequest.reject();
            bucketAccessRequestEntityRepository.save( bucketAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while rejecting the Bucket Access Request" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public Boolean addUserToBucketByBucketAdmin(UserBucketMapping addUserFromUiToBucket) {
        log.info( "Inside addUserToBucketByBucketAdmin" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( addUserFromUiToBucket.getBucketName() ) );
            UserAggregate user = Objects.requireNonNull( userAggregateRepository.findByUserName( addUserFromUiToBucket.getUserName() ) );
            BucketAccessEntity readAndWriteAccess = Objects.requireNonNull( bucketAccessEntityRepository.findByReadAndWrite( true, true ) );
            bucket.addBucketAccessingUsers( user.getUserId(), readAndWriteAccess.getBucketAccessId() );
            bucket.addBucketObject( user.getUserName() + "/", user.getUserId() );
            bucketAggregateRepository.save( bucket );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while adding user to the Bucket by admin" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public Status removeUserFromBucketByBucketAdmin(UserBucketMapping removeUserFromBucket) {
        log.info( "Inside removeUserToBucketByBucketAdmin" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( removeUserFromBucket.getBucketName() ) );
            UserAggregate user = Objects.requireNonNull( userAggregateRepository.findByUserName( removeUserFromBucket.getUserName() ) );
            Boolean userExistsInBucket = bucket.isUserExistsInBucket( user.getUserId() );
            System.out.println( "userExistsInBucket--------->" + userExistsInBucket );
            bucket.getBucketAccessingUsers().forEach( bucketAccessingUser ->
                    System.out.println( "new Bucket Aggregate---before---->" + bucketAccessingUser.getUserId() + " " +
                            bucketAccessingUser.getBucketAccessId() + " " + bucketAccessingUser.getBucket().getBucketName()
                    )
            );
            bucket.getBucketObjects().forEach( bucketObjectAggregate ->
                    System.out.println( "new Bucket object Aggregate---before---->" + bucketObjectAggregate.getBucketObjectName() + " " +
                            bucketObjectAggregate.getOwnerId() + " " + bucketObjectAggregate.getAccessingUsers().size()
                    )
            );
            int bucketObjectsCount = bucket.getBucketObjects( user.getUserId() );
            System.out.println( "bucketObjectsCount--------->" + bucketObjectsCount );
            if (userExistsInBucket && bucketObjectsCount == 1) {
                boolean removedUser = bucket.removeBucketAccessingUsers( user.getUserId() );
                boolean removedObject = bucket.removeBucketObject( user.getUserName() + "/", user.getUserId() );
                if (removedUser && removedObject) {
                    bucketAggregateRepository.save( bucket );
                    status.setValue( Boolean.TRUE );
                }
            } else {
                System.out.println( "inside" );
                status.setValue( Boolean.FALSE );
                status.setReasonForFailure( "Either User not exists or user has more Bucket Objects which has to be deleted before removing the user" );
            }
        } catch (Exception e) {
            log.error( "Exception while removing user from the Bucket by admin" + e );
            status.setValue( Boolean.FALSE );
        }
        return status;
    }

    public List<BucketAccessRequestEntity> getBucketAccessRequestForAdmin() {
        return bucketAccessRequestEntityRepository.findAll(); // not sure of this
    }

    public List<BucketAccessRequestEntity> getBucketAccessRequestForUser(String userName) {
        return bucketAccessRequestEntityRepository.findAllByUserId( userAggregateRepository.findByUserName( userName ).getUserId() );
    }

}
