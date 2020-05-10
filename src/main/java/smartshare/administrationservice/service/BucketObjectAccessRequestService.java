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


    private Boolean deleteBucketObjectAccessRequest(BucketObjectAccessRequestDto bucketObjectAccessRequest) {
        log.info( "Inside deleteBucketObjectAccessRequest" );
        try {
            Optional<BucketObjectAccessRequestEntity> bucketObjectAccessRequestEntityExists = bucketObjectAccessRequestEntityRepository.findById( bucketObjectAccessRequest.getId() );
            if (bucketObjectAccessRequestEntityExists.isPresent()) {
                bucketObjectAccessRequestEntityRepository.delete( bucketObjectAccessRequestEntityExists.get() );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Error in deleting the object access request :" + bucketObjectAccessRequest.getBucketObjectName() + " Error:" + e.getMessage() );
        }
        return Boolean.FALSE;
    }

    public boolean deleteBucketObjectAccessRequests(List<BucketObjectAccessRequestDto> bucketObjectAccessRequests) {
        log.info( "Inside rejectBucketObjectAccessRequests" );
        try {
            return bucketObjectAccessRequests.stream()
                    .map( this::deleteBucketObjectAccessRequest )
                    .noneMatch( saveResult -> saveResult.equals( Boolean.FALSE ) );
        } catch (Exception e) {
            log.error( "Error in deleting the object access requests " + e );
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
    public Boolean approveBucketObjectAccessRequest(BucketObjectAccessRequestDto bucketObjectAccessRequest) {
        log.info( "Inside approveBucketObjectAccessRequest" );
        try {
            Optional<BucketObjectAccessRequestEntity> bucketObjectAccessRequestEntityExists = bucketObjectAccessRequestEntityRepository.findById( bucketObjectAccessRequest.getId() );
            if (bucketObjectAccessRequestEntityExists.isPresent()) {
                Optional<BucketObjectAggregate> bucketObjectExists = bucketObjectAggregateRepository.findById( bucketObjectAccessRequestEntityExists.get().getBucketObjectId() );
                BucketObjectAggregate bucketObject = null;
                if (bucketObjectExists.isPresent() && Boolean.TRUE.equals( bucketObjectExists.get().isUserExistsInBucketObject( bucketObjectAccessRequestEntityExists.get().getUserId() ) )) {
                    bucketObject = updateBucketObjectAccessEntries( bucketObjectExists.get(), bucketObjectAccessRequestEntityExists.get() );
                } else if (bucketObjectExists.isPresent()) {
                    bucketObject = insertBucketObjectAccessEntries( bucketObjectExists.get(), bucketObjectAccessRequestEntityExists.get() );
                }
                bucketObjectAggregateRepository.save( Objects.requireNonNull( bucketObject ) );
                bucketObjectAccessRequestEntityRepository.save( bucketObjectAccessRequestEntityExists.get().approve() );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Error in approving the object access requests :" + bucketObjectAccessRequest.getBucketObjectName() + " Error:" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public boolean approveBucketObjectAccessRequests(List<BucketObjectAccessRequestDto> bucketObjectAccessRequests) {
        log.info( "Inside approveBucketObjectAccessRequests" );
        try {
            return bucketObjectAccessRequests.stream()
                    .map( this::approveBucketObjectAccessRequest )
                    .noneMatch( saveResult -> saveResult.equals( Boolean.FALSE ) );
        } catch (Exception e) {
            log.error( "Error in approving the object access requests " + e );
        }
        return Boolean.FALSE;
    }


    private Boolean rejectObjectAccessRequest(BucketObjectAccessRequestDto bucketObjectAccessRequest) {
        log.info( "Inside rejectObjectAccessRequest" );
        try {
            Optional<BucketObjectAccessRequestEntity> bucketObjectAccessRequestEntityExists = bucketObjectAccessRequestEntityRepository.findById( bucketObjectAccessRequest.getId() );
            if (bucketObjectAccessRequestEntityExists.isPresent()) {
                BucketObjectAccessRequestEntity rejectedObjectAccessRequest = bucketObjectAccessRequestEntityExists.get().reject();
                bucketObjectAccessRequestEntityRepository.save( rejectedObjectAccessRequest );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Error in rejecting the object access requests :" + bucketObjectAccessRequest.getBucketObjectName() + " Error:" + e.getMessage() );
        }
        return Boolean.FALSE;
    }

    public boolean rejectBucketObjectAccessRequests(List<BucketObjectAccessRequestDto> bucketObjectAccessRequests) {
        log.info( "Inside rejectBucketObjectAccessRequests" );
        try {
            return bucketObjectAccessRequests.stream()
                    .map( this::rejectObjectAccessRequest )
                    .noneMatch( saveResult -> saveResult.equals( Boolean.FALSE ) );
        } catch (Exception e) {
            log.error( "Error in rejecting the object access requests " + e );
        }
        return Boolean.FALSE;
    }


    private BucketObjectAccessRequestDto bucketObjectAccessRequestDtoMapper(BucketObjectAccessRequestEntity bucketObjectAccessRequestEntity) {
        BucketObjectAccessRequestDto bucketObjectAccessRequestDto = new BucketObjectAccessRequestDto();
        bucketObjectAccessRequestDto.setId( bucketObjectAccessRequestEntity.getId() );
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

    public List<BucketObjectAccessRequestDto> getAccessRequestsToBeApprovedByOwner(int ownerId) {
        log.info( "Inside getAccessRequestsToBeApprovedByOwnerOfObject" );
        // owner will be same
        return bucketObjectAccessRequestEntityRepository.findAllByOwnerId( ownerId ).stream()
                .map( this::bucketObjectAccessRequestDtoMapper )
                .collect( Collectors.toList() );
    }

    public List<BucketObjectAccessRequestDto> getAccessRequestsCreatedByUser(int userId) {
        log.info( "Inside getAccessRequestsCreatedByUser" );
        // user will be same
        return bucketObjectAccessRequestEntityRepository.findAllByUserId( userId ).stream()
                .map( this::bucketObjectAccessRequestDtoMapper )
                .collect( Collectors.toList() );
    }

    // not related to access request

    private List<FileComponent> extractAccessingUsers(BucketObjectAggregate bucketObjectAggregate, UserAggregate owner) {
        return bucketObjectAggregate.getAccessingUsers().stream()
                .filter( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() != owner.getUserId() )
                .map( bucketObjectAccessingUser -> {
                    Optional<UserAggregate> user = userAggregateRepository.findById( bucketObjectAccessingUser.getUserId() );
                    Optional<ObjectAccessEntity> access = objectAccessEntityRepository.findById( bucketObjectAccessingUser.getObjectAccessId() );
                    if (user.isPresent() && access.isPresent()) {
                        String accessInfo = (Boolean.TRUE.equals( access.get().getRead() ) ? "Read" : "") +
                                " " +
                                (Boolean.TRUE.equals( access.get().getWrite() ) ? "Write" : "") +
                                " " +
                                (Boolean.TRUE.equals( access.get().getDelete() ) ? "Delete" : "");
                        return new FileComponent( bucketObjectAggregate.getBucketObjectName(), owner.getUserName(), user.get().getUserName(), accessInfo );
                    }
                    return null;
                } )
                .filter( Objects::nonNull )
                .collect( Collectors.toList() );
    }

    private FolderComponent identifyNameOfOwnerFolder(BucketObjectAggregate bucketObject, FolderComponent previousFolder, UserAggregate owner) {
        List<String> previousFolderSplits = Arrays.asList( previousFolder.getCompleteName().split( "/" ) );
        List<String> currentFolderSplits = Arrays.asList( bucketObject.getBucketObjectName().split( "/" ) );
        String name = "";
        if (previousFolderSplits.isEmpty()) name = bucketObject.getBucketObjectName().replace( "/", "" );
        else {
            if (!previousFolderSplits.get( previousFolderSplits.size() - 1 ).equals( currentFolderSplits.get( currentFolderSplits.size() - 1 ) )) {
                if (previousFolderSplits.size() == currentFolderSplits.size())
                    return (FolderComponent) previousFolder.getParent().add( new FolderComponent( currentFolderSplits.get( currentFolderSplits.size() - 1 ), bucketObject.getBucketObjectName(), owner.getUserName(), previousFolder.getParent() ) );
                name = currentFolderSplits.get( currentFolderSplits.size() - 1 );

            }
        }

        return (FolderComponent) previousFolder.add( new FolderComponent( name, bucketObject.getBucketObjectName(), owner.getUserName(), previousFolder ) );

    }


    private FolderComponent ownerFileStructureConverter(List<BucketObjectAggregate> bucketObjects, String bucketName, UserAggregate owner) {
        log.info( "Inside ownerFileStructureConverter" );

        // Forming the root node

        FolderComponent root = new FolderComponent( bucketName, "/", owner.getUserName(), null );
        FolderComponent previousFolder = root;

        for (BucketObjectAggregate bucketObject : bucketObjects) {


            if (fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches() && (!bucketObject.getBucketObjectName().contains( "/" ))) {
                root.addAll( extractAccessingUsers( bucketObject, owner ) );
            } else {
                if (bucketObject.getBucketObjectName().endsWith( "/" ))
                    previousFolder = identifyNameOfOwnerFolder( bucketObject, previousFolder, owner );
                else {
                    previousFolder.addAll( extractAccessingUsers( bucketObject, owner ) );
                }
            }
        }
        return root;

    }


    public FolderComponent getListOfUsersAccessingOwnerObjects(String bucketName, int ownerId) {
        log.info( "Inside getListOfUsersAccessingOwnerObject" );
        Optional<UserAggregate> owner = userAggregateRepository.findById( ownerId );
        if (owner.isPresent()) {
            BucketAggregate bucket = bucketAggregateRepository.findByBucketName( bucketName );
            final List<BucketObjectAggregate> bucketObjectAggregateStream = bucket.getBucketObjects().stream()
                    .filter( bucketObjectAggregate -> bucketObjectAggregate.getOwnerId() == owner.get().getUserId() )
                    .sorted( Comparator.comparing( BucketObjectAggregate::getBucketObjectName ) )
                    .collect( Collectors.toList() );
            return this.ownerFileStructureConverter( bucketObjectAggregateStream, bucketName, owner.get() );
        }
        return null;
    }


    private UserFolderComponent extractAccess(BucketObjectAggregate bucketObjectAggregate, int userId, UserFolderComponent previousFolder) {
        log.info( "Inside extractAccess" );
        return bucketObjectAggregate.getAccessingUsers().stream()
                .filter( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() == userId )
                .findAny()
                .map( bucketObjectAccessingUser -> {
                    Optional<ObjectAccessEntity> access = objectAccessEntityRepository.findById( bucketObjectAccessingUser.getObjectAccessId() );
                    UserFolderComponent userFolderComponent = (null != previousFolder) ? new UserFolderComponent( bucketObjectAggregate.getBucketObjectName(), bucketObjectAggregate.getBucketObjectName(), previousFolder.getParent() ) : new UserFolderComponent( bucketObjectAggregate.getBucketObjectName(), bucketObjectAggregate.getBucketObjectName(), null );
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
                .orElseGet( () -> new UserFolderComponent( bucketObjectAggregate.getBucketObjectName(), bucketObjectAggregate.getBucketObjectName(), previousFolder.getParent() ) );
    }


    private UserFolderComponent identifyNameOfFolder(BucketObjectAggregate bucketObjectAggregate, UserFolderComponent previousFolder) {

        List<String> previousFolderSplits = Arrays.asList( previousFolder.getCompleteName().split( "/" ) );
        List<String> currentFolderSplits = Arrays.asList( bucketObjectAggregate.getBucketObjectName().split( "/" ) );
        String name = "";

        if (previousFolderSplits.isEmpty()) name = bucketObjectAggregate.getBucketObjectName().replace( "/", "" );
        else {
            if (!previousFolderSplits.get( previousFolderSplits.size() - 1 ).equals( currentFolderSplits.get( currentFolderSplits.size() - 1 ) )) {
                if (previousFolderSplits.size() == currentFolderSplits.size())
                    return (UserFolderComponent) previousFolder.getParent().add( new UserFolderComponent( currentFolderSplits.get( currentFolderSplits.size() - 1 ), bucketObjectAggregate.getBucketObjectName(), previousFolder.getParent() ) );
                name = currentFolderSplits.get( currentFolderSplits.size() - 1 );
            }
        }
        return (UserFolderComponent) previousFolder.add( new UserFolderComponent( name, bucketObjectAggregate.getBucketObjectName(), previousFolder ) );
    }

    private Boolean checkPreviousFolderInFile(String previousFolderName, String currentObjectName) {
        List<String> previousFolderSplits = Arrays.asList( previousFolderName.split( "/" ) );
        List<String> currentFolderSplits = Arrays.asList( currentObjectName.split( "/" ) );
        return previousFolderSplits.get( previousFolderSplits.size() - 1 ).equals( currentFolderSplits.get( currentFolderSplits.size() - 2 ) );
    }


    private UserFolderComponent userFileStructureConverter(List<BucketObjectAggregate> bucketObjects, String bucketName, UserAggregate user) {
        log.info( "Inside userFileStructureConverter" );

        // Forming the root node

        UserFolderComponent root = new UserFolderComponent( bucketName, "/", null );
        UserFolderComponent previousFolder = root;
        for (BucketObjectAggregate bucketObject : bucketObjects) {

            if (fileExtensionRegex.matcher( bucketObject.getBucketObjectName() ).matches() && (!bucketObject.getBucketObjectName().contains( "/" ))) {
                root.add( extractAccess( bucketObject, user.getUserId(), previousFolder ) );
            } else {
                if (bucketObject.getBucketObjectName().endsWith( "/" ))
                    previousFolder = identifyNameOfFolder( bucketObject, previousFolder );
                else {
                    if (checkPreviousFolderInFile( previousFolder.getCompleteName(), bucketObject.getBucketObjectName() )) {
                        previousFolder.add( extractAccess( bucketObject, user.getUserId(), previousFolder ) );
                    } else {
                        previousFolder.getParent().add( extractAccess( bucketObject, user.getUserId(), previousFolder ) );
                    }
                }
            }
        }
        return root;
    }


    public UserFolderComponent getUserFilesByBucket(String bucketName, int userId) {
        log.info( "Inside getUserFilesByBucket" );
        BucketAggregate bucket = bucketAggregateRepository.findByBucketName( bucketName );
        Optional<UserAggregate> userExist = userAggregateRepository.findById( userId );
        if (userExist.isPresent()) {
            final List<BucketObjectAggregate> bucketObjectAggregate = bucket.getBucketObjects().stream()
                    .sorted( Comparator.comparing( BucketObjectAggregate::getBucketObjectName ) )
                    .collect( Collectors.toList() );
            return userFileStructureConverter( bucketObjectAggregate, bucketName, userExist.get() );
        }
        return null;
    }


}
