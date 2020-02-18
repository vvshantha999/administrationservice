package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.dto.UsersAccessingOwnerObject;
import smartshare.administrationservice.dto.mappers.ObjectAccessRequestMapper;
import smartshare.administrationservice.models.AccessingUser;
import smartshare.administrationservice.models.BucketObject;
import smartshare.administrationservice.models.ObjectAccess;
import smartshare.administrationservice.models.ObjectAccessRequest;
import smartshare.administrationservice.repository.BucketObjectRepository;
import smartshare.administrationservice.repository.ObjectAccessRepository;
import smartshare.administrationservice.repository.ObjectAccessRequestRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ObjectAccessRequestService {

    private ObjectAccessRequestRepository objectAccessRequestRepository;
    private BucketObjectRepository bucketObjectRepository;
    private ObjectAccessRequestMapper objectAccessRequestMapper;
    private ObjectAccessRepository objectAccessRepository;

    @Autowired
    ObjectAccessRequestService(ObjectAccessRequestRepository objectAccessRequestRepository,
                               BucketObjectRepository bucketObjectRepository,
                               ObjectAccessRequestMapper objectAccessRequestMapper,
                               ObjectAccessRepository objectAccessRepository) {
        this.objectAccessRequestRepository = objectAccessRequestRepository;
        this.bucketObjectRepository = bucketObjectRepository;
        this.objectAccessRequestMapper = objectAccessRequestMapper;
        this.objectAccessRepository = objectAccessRepository;
    }

    public Boolean createObjectAccessRequest(List<ObjectAccessRequestFromUi> objectAccessRequestsFromUi) {

        log.info( "Inside createObjectAccessRequest" );

        try {
            List<ObjectAccessRequest> objectAccessRequests = objectAccessRequestsFromUi.stream()
                    .map( objectAccessRequestFromUi -> (ObjectAccessRequest) objectAccessRequestMapper.map( objectAccessRequestFromUi ) )
                    .collect( Collectors.toList() );
            List<ObjectAccessRequest> createdObjectAccessRequest = objectAccessRequestRepository.saveAll( objectAccessRequests );
            if (createdObjectAccessRequest.size() == objectAccessRequests.size()) {
//                objectAccessRequestRepository.flush();
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Error while createObjectAccessRequest  " + e.getMessage(), e );
        }
        return Boolean.FALSE; // have to test jpa update
    }

    public Boolean deleteObjectAccessRequest(List<ObjectAccessRequest> objectAccessRequests) {
        log.info( "Inside deleteObjectAccessRequest" );
            try {
                objectAccessRequestRepository.deleteInBatch( objectAccessRequests );
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error( "Error in deleting the object access requests " + e.getMessage() );
            }
        return Boolean.FALSE;
    }

    private List<Boolean> mergeAccess(ObjectAccess currentAccess, ObjectAccess newAccess) {
        log.info( "Inside mergeAccess" );
        List<Boolean> currentAccessList = currentAccess.toList();
        List<Boolean> newAccessList = newAccess.toList();
        List<Boolean> collect = newAccessList.stream()
                .map( access -> (access || currentAccessList.get( newAccessList.indexOf( access ) )) )
                .collect( Collectors.toList() );
        return collect;
    }

    private Optional<BucketObject> updateObjectAccessingUserList(ObjectAccessRequest approvedObjectAccessRequest) {
        log.info( "Inside updateObjectAccessingUserList" );

        AccessingUser newAccessingUserToBeAdded = new AccessingUser(
                approvedObjectAccessRequest.getUser(),
                approvedObjectAccessRequest.getBucketObject(),
                approvedObjectAccessRequest.getAccess() );

        List<AccessingUser> accessingUsers = approvedObjectAccessRequest.getBucketObject().getAccessingUsers();
        if ((!accessingUsers.isEmpty())) {
            for (AccessingUser accessingUser : accessingUsers) {
                if (accessingUser.getUser().getUserName() == approvedObjectAccessRequest.getUser().getUserName()) {
                    List<Boolean> mergedAccess = mergeAccess( accessingUser.getAccess(), approvedObjectAccessRequest.getAccess() );
                    accessingUser.setAccess( objectAccessRepository.findByReadAndWriteAndDelete( mergedAccess.get( 0 ), mergedAccess.get( 1 ), mergedAccess.get( 2 ) ) );
                } else {
                    approvedObjectAccessRequest.getBucketObject().addAccessingUser( newAccessingUserToBeAdded );
                }
            }

        } else {
            approvedObjectAccessRequest.getBucketObject().setAccessingUsers( Collections.singletonList( newAccessingUserToBeAdded ) );
        }
        return Optional.of( bucketObjectRepository.save( approvedObjectAccessRequest.getBucketObject() ) );
    }

    public Boolean approveObjectAccessRequest(ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        try {
            ObjectAccessRequest approvedObjectAccessRequest = objectAccessRequest.approve();
            objectAccessRequestRepository.save( approvedObjectAccessRequest );
            return (updateObjectAccessingUserList( approvedObjectAccessRequest ).isPresent()) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            log.error( "Error in approving the object access requests " + e.getMessage() );
        }
        return Boolean.FALSE;
        // have to test whether the bucket object contains this added user or not;
    }

    public Boolean rejectObjectAccessRequest(ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        try {
            ObjectAccessRequest rejectedObjectAccessRequest = objectAccessRequest.reject();
            ObjectAccessRequest rejectedObjectAccessRequestSaveOperationResult = objectAccessRequestRepository.save( rejectedObjectAccessRequest );
            return rejectedObjectAccessRequestSaveOperationResult.getId().equals( objectAccessRequest.getId() ) ? Boolean.TRUE : Boolean.FALSE; // have to check if condition for status

        } catch (Exception e) {
            log.error( "Error in rejecting the object access requests " + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    private UsersAccessingOwnerObject changeDataFormatForUi(BucketObject bucketObject) {
        return new UsersAccessingOwnerObject( bucketObject );
    }

    public List<UsersAccessingOwnerObject> getListOfUsersAccessingOwnerObject(String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        List<BucketObject> bucketObjectsBelongingToOwner = bucketObjectRepository.findAllByOwner( ownerName );
        List<UsersAccessingOwnerObject> listOfUsersAccessingOwnerObject = bucketObjectsBelongingToOwner.stream()
                .map( this::changeDataFormatForUi )
                .collect( Collectors.toList() );
        System.out.println( "listOfUsersAccessingOwnerObject---->" + listOfUsersAccessingOwnerObject );
        return listOfUsersAccessingOwnerObject;
    }

    public List<ObjectAccessRequest> getAccessRequestsCreatedByUser(String userName) {
        log.info( "Inside getAccessRequestsCreatedByUser" );
        List<ObjectAccessRequest> accessRequests = objectAccessRequestRepository.findAllByUser( userName );
        System.out.println( "accessRequests------------->" + accessRequests );
        return accessRequests;
    }

    public List<ObjectAccessRequest> getAccessRequestsToBeApprovedByOwnerOfObject(String ownerName) {
        log.info( "Inside getAccessRequestsToBeApprovedByOwnerOfObject" );
        List<ObjectAccessRequest> accessRequestsToBeApprovedByOwnerOfObject = objectAccessRequestRepository.findObjectAccessRequestsByOwnerAndStatus( ownerName, StatusConstants.INPROGRESS.toString() );
        System.out.println( "accessRequestsToBeApprovedByOwnerOfObject-------->" + accessRequestsToBeApprovedByOwnerOfObject );
        return accessRequestsToBeApprovedByOwnerOfObject;
    }
}
