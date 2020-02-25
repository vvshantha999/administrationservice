package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.dto.UserAccessingObject;
import smartshare.administrationservice.dto.UsersAccessingOwnerObject;
import smartshare.administrationservice.dto.mappers.ObjectAccessRequestMapper;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BucketObjectAccessRequestService {


    private ObjectAccessRequestMapper objectAccessRequestMapper;
    private BucketObjectAccessRequestEntityRepository bucketObjectAccessRequestEntityRepository;
    private BucketAggregateRepository bucketAggregateRepository;
    private BucketObjectAggregateRepository bucketObjectAggregateRepository;
    private ObjectAccessEntityRepository objectAccessEntityRepository;
    private UserAggregateRepository userAggregateRepository;

    @Autowired
    BucketObjectAccessRequestService(
            ObjectAccessRequestMapper objectAccessRequestMapper,
            BucketObjectAccessRequestEntityRepository bucketObjectAccessRequestEntityRepository,
            BucketAggregateRepository bucketAggregateRepository,
            BucketObjectAggregateRepository bucketObjectAggregateRepository,
            ObjectAccessEntityRepository objectAccessEntityRepository,
            UserAggregateRepository userAggregateRepository
    ) {
        this.objectAccessRequestMapper = objectAccessRequestMapper;
        this.bucketObjectAccessRequestEntityRepository = bucketObjectAccessRequestEntityRepository;
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.bucketObjectAggregateRepository = bucketObjectAggregateRepository;
        this.objectAccessEntityRepository = objectAccessEntityRepository;
        this.userAggregateRepository = userAggregateRepository;
    }


    private BucketObjectAccessRequestEntity createBucketObjectAccessRequest(ObjectAccessRequestFromUi objectAccessRequestFromUi) {
        return objectAccessRequestMapper.map( objectAccessRequestFromUi );
    }


    public Boolean createBucketObjectAccessRequests(List<ObjectAccessRequestFromUi> objectAccessRequestsFromUi) {

        log.info( "Inside createBucketObjectAccessRequests" );

        try {
            List<BucketObjectAccessRequestEntity> objectAccessRequests = objectAccessRequestsFromUi.stream()
                    .map( this::createBucketObjectAccessRequest )
                    .collect( Collectors.toList() );
            List<BucketObjectAccessRequestEntity> result = bucketObjectAccessRequestEntityRepository.saveAll( objectAccessRequests );
            if (result.size() == objectAccessRequests.size()) return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Error while createObjectAccessRequest  " + e.getMessage(), e );
        }
        return Boolean.FALSE;
    }


    public Boolean deleteBucketObjectAccessRequest(BucketObjectAccessRequestEntity objectAccessRequest) {
        log.info( "Inside deleteBucketObjectAccessRequest" );
        try {
            bucketObjectAccessRequestEntityRepository.delete( objectAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Error in deleting the object access requests " + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    private Integer mergedAccess(int currentAccessId, int newAccessId) {
        log.info( "Inside mergeAccess" );
        Optional<ObjectAccessEntity> currentAccess = this.objectAccessEntityRepository.findById( currentAccessId );
        Optional<ObjectAccessEntity> newAccess = this.objectAccessEntityRepository.findById( newAccessId );
        List<Boolean> mergedAccess = new ArrayList<>();
        if (currentAccess.isPresent() && newAccess.isPresent()) {
            List<Boolean> currentAccessList = currentAccess.get().toList();
            List<Boolean> newAccessList = newAccess.get().toList();
            for (int i = 0; i < newAccessList.size(); i++) {
                mergedAccess.add( (newAccessList.get( i ) || currentAccessList.get( i )) );
            }
            return objectAccessEntityRepository.findByReadAndWriteAndDelete( mergedAccess.get( 0 ), mergedAccess.get( 1 ), mergedAccess.get( 2 ) ).getObjectAccessId();
        }
        return null;
    }

    private BucketObjectAggregate updateBucketObjectAccessEntries(BucketObjectAggregate bucketObject, BucketObjectAccessRequestEntity objectAccessRequest) {

        for (BucketObjectAccessingUser accessingUser : bucketObject.getAccessingUsers()) {
            if (accessingUser.getUserId() == objectAccessRequest.getUserId()) {
                accessingUser.setObjectAccessId( Objects.requireNonNull( mergedAccess( accessingUser.getObjectAccessId(), objectAccessRequest.getObjectAccessId() ) ) );
                return bucketObject;
            }
        }
        return null;
    }

    private BucketObjectAggregate insertBucketObjectAccessEntries(BucketObjectAggregate bucketObject, BucketObjectAccessRequestEntity objectAccessRequest) {
        log.info( "Inside insertBucketObjectAccessEntries" );
        return bucketObject.addAccessingUser( objectAccessRequest.getUserId(), objectAccessRequest.getObjectAccessId() );
    }


    @Transactional
    public Boolean approveBucketObjectAccessRequest(BucketObjectAccessRequestEntity objectAccessRequest) {
        log.info( "Inside approveBucketObjectAccessRequest" );
        try {
            Optional<BucketAggregate> bucket = bucketAggregateRepository.findById( objectAccessRequest.getBucketId() );
            if (bucket.isPresent()) {
                BucketObjectAggregate bucketObject = Objects.requireNonNull( bucketObjectAggregateRepository.findByBucketObjectIdAndBucket_BucketId( objectAccessRequest.getBucketObjectId(), bucket.get().getBucketId() ) );
                // BucketObjectAggregate bucketObject = Objects.requireNonNull(bucket.get().findBucketObjectByBucketObjectId( objectAccessRequest.getBucketObjectId() ));
                if (bucketObject.isUserExistsInBucketObject( objectAccessRequest.getUserId() ))
                    bucketObject = updateBucketObjectAccessEntries( bucketObject, objectAccessRequest );
                else
                    bucketObject = insertBucketObjectAccessEntries( bucketObject, objectAccessRequest );
                bucketObjectAggregateRepository.save( Objects.requireNonNull( bucketObject ) );
                bucketObjectAccessRequestEntityRepository.save( objectAccessRequest.approve() );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Error in approving the object access requests " + e );
        }
        return Boolean.FALSE;
    }


    public Boolean rejectObjectAccessRequest(BucketObjectAccessRequestEntity objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        try {
            BucketObjectAccessRequestEntity rejectedObjectAccessRequest = objectAccessRequest.reject();
            bucketObjectAccessRequestEntityRepository.save( rejectedObjectAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Error in rejecting the object access requests " + e.getMessage() );
        }
        return Boolean.FALSE;
    }

    public List<BucketObjectAccessRequestEntity> getAccessRequestsCreatedByUser(String userName) {
        log.info( "Inside getAccessRequestsCreatedByUser" );
        UserAggregate user = userAggregateRepository.findByUserName( userName );
        List<BucketObjectAccessRequestEntity> accessRequests = bucketObjectAccessRequestEntityRepository.findAllByUserId( user.getUserId() );
        System.out.println( "accessRequests------------->" + accessRequests );
        return accessRequests;
    }

    public List<BucketObjectAccessRequestEntity> getAccessRequestsToBeApprovedByOwnerOfObject(String ownerName) {
        log.info( "Inside getAccessRequestsToBeApprovedByOwnerOfObject" );
        UserAggregate owner = userAggregateRepository.findByUserName( ownerName );
        List<BucketObjectAccessRequestEntity> accessRequestsToBeApprovedByOwnerOfObject = bucketObjectAccessRequestEntityRepository.findAllByOwnerIdAndStatus( owner.getUserId(), StatusConstants.INPROGRESS.toString() );
        System.out.println( "accessRequestsToBeApprovedByOwnerOfObject-------->" + accessRequestsToBeApprovedByOwnerOfObject );
        return accessRequestsToBeApprovedByOwnerOfObject;
    }

    // not related to access request

    private List<UserAccessingObject> extractAccessingUsers(BucketObjectAggregate bucketObjectAggregate) {
        return bucketObjectAggregate.getAccessingUsers().stream()
                .map( bucketObjectAccessingUser -> {
                    Optional<UserAggregate> user = userAggregateRepository.findById( bucketObjectAccessingUser.getUserId() );
                    Optional<ObjectAccessEntity> access = objectAccessEntityRepository.findById( bucketObjectAccessingUser.getObjectAccessId() );
                    if (user.isPresent() && access.isPresent()) {
                        String accessInfo = (access.get().getRead() ? "Read" : "") +
                                " " +
                                (access.get().getWrite() ? "Write" : "") +
                                " " +
                                (access.get().getDelete() ? "Delete" : "");
                        return new UserAccessingObject( user.get().getUserName(), accessInfo );
                    }
                    return null;
                } ).collect( Collectors.toList() );
    }


    public List<UsersAccessingOwnerObject> getListOfUsersAccessingOwnerObjects(String bucketName, String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        BucketAggregate bucket = bucketAggregateRepository.findByBucketName( bucketName );
        UserAggregate owner = userAggregateRepository.findByUserName( ownerName );
        return bucket.getBucketObjects().stream()
                .filter( bucketObjectAggregate -> bucketObjectAggregate.getOwnerId() == owner.getUserId() )
                .map( bucketObjectAggregate -> new UsersAccessingOwnerObject( bucketObjectAggregate.getBucketObjectName(), Objects.requireNonNull( extractAccessingUsers( bucketObjectAggregate ) ) ) )
                .collect( Collectors.toList() );


    }



}
