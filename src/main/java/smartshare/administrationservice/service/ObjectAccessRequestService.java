package smartshare.administrationservice.service;

import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.UsersAccessingOwnerObject;
import smartshare.administrationservice.models.AccessingUser;
import smartshare.administrationservice.models.BucketObject;
import smartshare.administrationservice.models.ObjectAccessRequest;
import smartshare.administrationservice.models.Status;
import smartshare.administrationservice.repository.BucketObjectRepository;
import smartshare.administrationservice.repository.ObjectAccessRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ObjectAccessRequestService {

    private ObjectAccessRequestRepository objectAccessRequestRepository;
    private BucketObjectRepository bucketObjectRepository;
    private Status statusOfOperation;

    ObjectAccessRequestService(ObjectAccessRequestRepository objectAccessRequestRepository, Status statusOfOperation, BucketObjectRepository bucketObjectRepository) {
        this.objectAccessRequestRepository = objectAccessRequestRepository;
        this.statusOfOperation = statusOfOperation;
        this.bucketObjectRepository = bucketObjectRepository;
    }

    public Status createObjectAccessRequest(List<ObjectAccessRequest> objectAccessRequests) {

        log.info( "Inside createObjectAccessRequest" );

        if (!objectAccessRequests.isEmpty()) {
            List<ObjectAccessRequest> createdObjectAccessRequest = objectAccessRequestRepository.saveAll( objectAccessRequests );
            List<String> createdObjectAccessRequests = createdObjectAccessRequest.stream().map( objectAccessRequest -> objectAccessRequest.getId() > 0 ? StatusConstants.SUCCESS.toString() : StatusConstants.FAILED.toString() ).collect( Collectors.toList() );
            if (!createdObjectAccessRequests.contains( StatusConstants.FAILED.toString() )) {
                statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
                objectAccessRequestRepository.flush();
            } else {
                statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
            }
        }
        return statusOfOperation; // have to test jpa update
    }

    public Status deleteObjectAccessRequest(List<ObjectAccessRequest> objectAccessRequests) {
        log.info( "Inside deleteObjectAccessRequest" );

        if (!objectAccessRequests.isEmpty()) {
            try {
                objectAccessRequestRepository.deleteInBatch( objectAccessRequests );
                statusOfOperation.setMessage( StatusConstants.SUCCESS.toString() );
            } catch (Exception e) {
                log.error( "Error in deleting the object access requests " + e.getMessage() );
                statusOfOperation.setMessage( StatusConstants.FAILED.toString() );
            }
        }
        return statusOfOperation;
    }

    private BucketObject updateObjectAccessingUserList(ObjectAccessRequest approvedObjectAccessRequest) {
        Log.info( "Inside updateObjectAccessingUserList" );
        AccessingUser newAccessingUserToBeAdded = new AccessingUser(
                approvedObjectAccessRequest.getUser(),
                approvedObjectAccessRequest.getBucketObject(),
                approvedObjectAccessRequest.getAccess() );
        BucketObject bucketObjectToBeUpdated = approvedObjectAccessRequest.getBucketObject().addAccessingUser( newAccessingUserToBeAdded );
        BucketObject creatingAccessingUserUnderGivenObject = bucketObjectRepository.saveAndFlush( bucketObjectToBeUpdated );
        System.out.println( "creatingAccessingUserUnderGivenObject----->" + creatingAccessingUserUnderGivenObject.getAccessingUsers() );
        return creatingAccessingUserUnderGivenObject;
    }

    public Status approveObjectAccessRequest(ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        ObjectAccessRequest approvedObjectAccessRequest = objectAccessRequest.approve();
        objectAccessRequestRepository.saveAndFlush( approvedObjectAccessRequest );
        statusOfOperation.setMessage( updateObjectAccessingUserList( approvedObjectAccessRequest ) != null ? StatusConstants.SUCCESS.toString() : StatusConstants.FAILED.toString() );
        // have to test whether the bucket object contains this added user or not;
        return statusOfOperation;
    }

    public Status rejectObjectAccessRequest(ObjectAccessRequest objectAccessRequest) {
        log.info( "Inside approveObjectAccessRequest" );
        ObjectAccessRequest rejectedObjectAccessRequest = objectAccessRequest.reject();
        ObjectAccessRequest rejectedObjectAccessRequestSaveOperationResult = objectAccessRequestRepository.saveAndFlush( rejectedObjectAccessRequest );
        statusOfOperation.setMessage( rejectedObjectAccessRequestSaveOperationResult.getId().equals( objectAccessRequest.getId() ) ? StatusConstants.SUCCESS.toString() : StatusConstants.FAILED.toString() ); // have to check if condition for status
        return statusOfOperation;
    }


    private UsersAccessingOwnerObject changeDataFormatForUi(BucketObject bucketObject) {
        return new UsersAccessingOwnerObject( bucketObject );
    }

    public List<UsersAccessingOwnerObject> getListOfUsersAccessingOwnerObject(String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        List<BucketObject> bucketObjectsBelongingToOwner = bucketObjectRepository.findAllByOwner( ownerName );
        List<UsersAccessingOwnerObject> listOfUsersAccessingOwnerObject = bucketObjectsBelongingToOwner.stream().map( this::changeDataFormatForUi ).collect( Collectors.toList() );
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
