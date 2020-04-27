package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartshare.administrationservice.dto.ObjectAccessRequest;
import smartshare.administrationservice.dto.mappers.ObjectAccessRequestMapper;
import smartshare.administrationservice.dto.response.BucketObjectAccessRequestDto;
import smartshare.administrationservice.dto.response.ownertree.FileComponent;
import smartshare.administrationservice.dto.response.ownertree.FolderComponent;
import smartshare.administrationservice.dto.response.usertree.UserFileComponent;
import smartshare.administrationservice.dto.response.usertree.UserFolderComponent;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.*;

import java.util.*;
import java.util.regex.Pattern;
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
    private Pattern fileExtensionRegex = Pattern.compile( "(.*)([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(\\..*)$" );

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


    private BucketObjectAccessRequestEntity createBucketObjectAccessRequest(ObjectAccessRequest objectAccessRequest) {
        return objectAccessRequestMapper.map( objectAccessRequest );
    }


    public Boolean createBucketObjectAccessRequests(List<ObjectAccessRequest> objectAccessRequestsFromUi) {

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
                if (Boolean.TRUE.equals( bucketObject.isUserExistsInBucketObject( objectAccessRequest.getUserId() ) ))
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


    private BucketObjectAccessRequestDto bucketObjectAccessRequestDtoMapper(BucketObjectAccessRequestEntity bucketObjectAccessRequestEntity) {
        BucketObjectAccessRequestDto bucketObjectAccessRequestDto = new BucketObjectAccessRequestDto();
        userAggregateRepository.findById( bucketObjectAccessRequestEntity.getUserId() )
                .ifPresent( userAggregate -> bucketObjectAccessRequestDto.setUserName( userAggregate.getUserName() ) );
        userAggregateRepository.findById( bucketObjectAccessRequestEntity.getOwnerId() )
                .ifPresent( userAggregate -> bucketObjectAccessRequestDto.setOwnerName( userAggregate.getUserName() ) );
        bucketAggregateRepository.findById( bucketObjectAccessRequestEntity.getBucketId() )
                .ifPresent( bucketAggregate -> bucketObjectAccessRequestDto.setBucketName( bucketAggregate.getBucketName() ) );
        bucketObjectAggregateRepository.findById( bucketObjectAccessRequestEntity.getBucketObjectId() )
                .ifPresent( bucketObjectAggregate -> bucketObjectAccessRequestDto.setBucketObjectName( bucketObjectAggregate.getBucketObjectName() ) );
        objectAccessEntityRepository.findById( bucketObjectAccessRequestEntity.getObjectAccessId() )
                .ifPresent( objectAccessEntity -> bucketObjectAccessRequestDto.setRequestType( objectAccessEntity.getAccessInfo() ) );
        bucketObjectAccessRequestDto.setStatus( bucketObjectAccessRequestEntity.getStatus() );
        return bucketObjectAccessRequestDto;
    }

    public List<BucketObjectAccessRequestDto> getAccessRequestsToBeApprovedByOwnerOfObject(String ownerName) {
        log.info( "Inside getAccessRequestsToBeApprovedByOwnerOfObject" );
        // owner will be same
        UserAggregate owner = userAggregateRepository.findByUserName( ownerName );
        return bucketObjectAccessRequestEntityRepository.findAllByOwnerId( owner.getUserId() ).stream()
                .map( this::bucketObjectAccessRequestDtoMapper )
                .collect( Collectors.toList() );

    }

    public List<BucketObjectAccessRequestDto> getAccessRequestsCreatedByUser(String userName) {
        log.info( "Inside getAccessRequestsCreatedByUser" );
        // user will be same
        UserAggregate user = userAggregateRepository.findByUserName( userName );
        return bucketObjectAccessRequestEntityRepository.findAllByUserId( user.getUserId() ).stream()
                .map( this::bucketObjectAccessRequestDtoMapper )
                .collect( Collectors.toList() );
    }

    // not related to access request

    private List<FileComponent> extractAccessingUsers(BucketObjectAggregate bucketObjectAggregate, String ownerName) {
        return bucketObjectAggregate.getAccessingUsers().stream()
                .map( bucketObjectAccessingUser -> {
                    Optional<UserAggregate> user = userAggregateRepository.findById( bucketObjectAccessingUser.getUserId() );
                    Optional<ObjectAccessEntity> access = objectAccessEntityRepository.findById( bucketObjectAccessingUser.getObjectAccessId() );
                    if (user.isPresent() && access.isPresent()) {
                        String accessInfo = (Boolean.TRUE.equals( access.get().getRead() ) ? "Read" : "") +
                                " " +
                                (Boolean.TRUE.equals( access.get().getWrite() ) ? "Write" : "") +
                                " " +
                                (Boolean.TRUE.equals( access.get().getDelete() ) ? "Delete" : "");
                        return new FileComponent( bucketObjectAggregate.getBucketObjectName(), ownerName, user.get().getUserName(), accessInfo );
                    }
                    return null;
                } )
                .filter( Objects::nonNull )
                .collect( Collectors.toList() );
    }


    private FolderComponent ownerFileStructureConverter(List<BucketObjectAggregate> bucketObjectAggregateStream, String bucketName, String ownerName) {
        log.info( "Inside ownerFileStructureConverter" );

        // Forming the root node

        FolderComponent root = new FolderComponent( bucketName, ownerName );
        FolderComponent previousFolder = root;
        for (BucketObjectAggregate bucketObject : bucketObjectAggregateStream) {

            // file in root folder
            if (fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches() && (!bucketObject.getBucketObjectName().contains( "/" ))) {
                root.addAll( extractAccessingUsers( bucketObject, ownerName ) );
                // root.add( new FileComponent( extractedKey.getKey(), currentKeyAccessInfo, owner, extractedKey.getKey(), extractedKey.getValue() ) );
            }

            //only folders and files within folders are allowed
            if (bucketObject.getBucketObjectName().endsWith( "/" ) || fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches()) {

                //first level of folder
                if (bucketObject.getBucketObjectName().endsWith( "/" ) && (previousFolder.getName().equals( bucketName ) || !bucketObject.getBucketObjectName().contains( previousFolder.getName() + "/" ))) {
                    previousFolder = (FolderComponent) root.add( new FolderComponent( bucketObject.getBucketObjectName().replace( "/", " " ).trim(), ownerName ) );
                } else // sub level in folders
                    if (bucketObject.getBucketObjectName().endsWith( "/" ) && bucketObject.getBucketObjectName().contains( previousFolder.getName() + "/" )) {
                        previousFolder = (FolderComponent) previousFolder.add( new FolderComponent( bucketObject.getBucketObjectName().replace( previousFolder.getName(), " " ).replace( "/", " " ).trim(), ownerName ) );
                    } else //file in sub level folders
                        if (fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches() && bucketObject.getBucketObjectName().contains( previousFolder.getName() + "/" )) {
                            previousFolder.addAll( extractAccessingUsers( bucketObject, ownerName ) );
                            // previousFolder.addAll( new FileComponent( extractedKey.getKey().replace( previousFolder.getCompleteName(), " " ).trim(), currentKeyAccessInfo, owner, extractedKey.getKey(), extractedKey.getValue() ) );
                        }
            } else {
                //file without extensions
                previousFolder.addAll( extractAccessingUsers( bucketObject, ownerName ) );
                // previousFolder.add( new FileComponent( extractedKey.getKey().replace( previousFolder.getCompleteName(), " " ).trim(), currentKeyAccessInfo, owner, extractedKey.getKey(), extractedKey.getValue() ) );
            }
        }
        return root;
    }

    public FolderComponent getListOfUsersAccessingOwnerObjects(String bucketName, String ownerName) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        BucketAggregate bucket = bucketAggregateRepository.findByBucketName( bucketName );
        UserAggregate owner = userAggregateRepository.findByUserName( ownerName );
        final List<BucketObjectAggregate> bucketObjectAggregateStream = bucket.getBucketObjects().stream()
                .filter( bucketObjectAggregate -> bucketObjectAggregate.getOwnerId() == owner.getUserId() )
                .sorted( Comparator.comparing( BucketObjectAggregate::getBucketObjectName ) )
                .collect( Collectors.toList() );

        return this.ownerFileStructureConverter( bucketObjectAggregateStream, bucketName, ownerName );
    }


    private UserFolderComponent extractAccess(BucketObjectAggregate bucketObjectAggregate, int userId) {
        log.info( "Inside extractAccess" );
        return bucketObjectAggregate.getAccessingUsers().stream()
                .filter( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() == userId )
                .findAny()
                .map( bucketObjectAccessingUser -> {
                    Optional<ObjectAccessEntity> access = objectAccessEntityRepository.findById( bucketObjectAccessingUser.getObjectAccessId() );
                    UserFolderComponent userFolderComponent = new UserFolderComponent( bucketObjectAggregate.getBucketObjectName() );
                    if (access.isPresent()) {
                        if (Boolean.TRUE.equals( access.get().getRead() ))
                            userFolderComponent.add( new UserFileComponent( "Read" ) );
                        if (Boolean.TRUE.equals( access.get().getWrite() ))
                            userFolderComponent.add( new UserFileComponent( "Write" ) );
                        if (Boolean.TRUE.equals( access.get().getDelete() ))
                            userFolderComponent.add( new UserFileComponent( "Delete" ) );
                    }
                    return userFolderComponent;
                } )
                .orElseGet( () -> new UserFolderComponent( bucketObjectAggregate.getBucketObjectName() ) );
    }

    private UserFolderComponent userFileStructureConverter(List<BucketObjectAggregate> bucketObjects, String bucketName, UserAggregate user) {
        log.info( "Inside userFileStructureConverter" );

        // Forming the root node

        UserFolderComponent root = new UserFolderComponent( bucketName );
        UserFolderComponent previousFolder = root;

        for (BucketObjectAggregate bucketObject : bucketObjects) {

            // file in root folder
            if (fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches() && (!bucketObject.getBucketObjectName().contains( "/" )))
                root.add( extractAccess( bucketObject, user.getUserId() ) );

            //only folders and files within folders are allowed
            if (bucketObject.getBucketObjectName().endsWith( "/" ) || fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches()) {

                //first level of folder
                if (bucketObject.getBucketObjectName().endsWith( "/" ) && (previousFolder.getName().equals( bucketName ) || !bucketObject.getBucketObjectName().contains( previousFolder.getName() + "/" ))) {
                    previousFolder = (UserFolderComponent) root.add( new UserFolderComponent( bucketObject.getBucketObjectName().replace( "/", " " ).trim() ) );
                } else // sub level in folders
                    if (bucketObject.getBucketObjectName().endsWith( "/" ) && bucketObject.getBucketObjectName().contains( previousFolder.getName() + "/" )) {
                        previousFolder = (UserFolderComponent) previousFolder.add( new UserFolderComponent( bucketObject.getBucketObjectName().replace( previousFolder.getName(), " " ).replace( "/", " " ).trim() ) );
                    } else //file in sub level folders
                        if (fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches() && bucketObject.getBucketObjectName().contains( previousFolder.getName() + "/" )) {
                            previousFolder.add( extractAccess( bucketObject, user.getUserId() ) );
                        }
            } else {
                //file without extensions
                previousFolder.add( extractAccess( bucketObject, user.getUserId() ) );
            }
        }
        return root;
    }


    public UserFolderComponent getUserFilesByBucket(String bucketName, String userName) {
        log.info( "Inside getUserFilesByBucket" );
        BucketAggregate bucket = bucketAggregateRepository.findByBucketName( bucketName );
        UserAggregate user = userAggregateRepository.findByUserName( userName );
        final List<BucketObjectAggregate> bucketObjectAggregate = bucket.getBucketObjects().stream()
                .sorted( Comparator.comparing( BucketObjectAggregate::getBucketObjectName ) )
                .collect( Collectors.toList() );
        return userFileStructureConverter( bucketObjectAggregate, bucketName, user );
    }
}
