package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.dto.UploadObject;
import smartshare.administrationservice.dto.UserBucketMapping;
import smartshare.administrationservice.dto.mappers.BucketAccessRequestMapper;
import smartshare.administrationservice.dto.response.BucketAccessRequestDto;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAccessEntityRepository;
import smartshare.administrationservice.repository.BucketAccessRequestEntityRepository;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BucketAccessRequestService {

    private final BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository;
    private final BucketAccessEntityRepository bucketAccessEntityRepository;
    private final BucketAggregateRepository bucketAggregateRepository;
    private final UserAggregateRepository userAggregateRepository;
    private final BucketAccessRequestMapper bucketAccessRequestMapper;
    private final CoreAPIService coreAPIService;

    private final Status status;

    @Autowired
    BucketAccessRequestService(
            BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository,
            Status status,
            BucketAccessEntityRepository bucketAccessEntityRepository,
            BucketAggregateRepository bucketAggregateRepository,
            UserAggregateRepository userAggregateRepository,
            BucketAccessRequestMapper bucketAccessRequestMapper, CoreAPIService coreAPIService) {

        this.bucketAccessRequestEntityRepository = bucketAccessRequestEntityRepository;
        this.status = status;
        this.bucketAccessEntityRepository = bucketAccessEntityRepository;
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.userAggregateRepository = userAggregateRepository;
        this.bucketAccessRequestMapper = bucketAccessRequestMapper;
        this.coreAPIService = coreAPIService;
    }


    @Transactional
    public Boolean createBucketAccessRequest(BucketAccessRequestFromUi bucketAccessRequestFromUi) {
        log.info( "Inside createBucketAccessRequest" );
        try {
            BucketAccessRequestEntity newBucketAccessRequest = bucketAccessRequestMapper.map( bucketAccessRequestFromUi );
            bucketAccessRequestEntityRepository.save( newBucketAccessRequest );
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error( "Exception while creating bucket Access Request" + e );
        }
        return Boolean.FALSE;
    }

    public Status deleteBucketAccessRequests(List<BucketAccessRequestDto> bucketAccessRequestDtos) {
        try {
            List<Boolean> deletedResults = bucketAccessRequestDtos.stream()
                    .map( bucketAccessRequestDto -> bucketAccessRequestEntityRepository.findById( bucketAccessRequestDto.getId() ) )
                    .map( bucketAccessRequestEntity -> {
                        if (bucketAccessRequestEntity.isPresent()) {
                            bucketAccessRequestEntityRepository.delete( bucketAccessRequestEntity.get() );
                            return Boolean.TRUE;
                        } else return Boolean.FALSE;
                    } )
                    .collect( Collectors.toList() );
            if (deletedResults.contains( Boolean.FALSE )) {
                status.setValue( Boolean.FALSE );
                status.setReasonForFailure( HttpStatus.NOT_FOUND.getReasonPhrase() );
            } else status.setValue( Boolean.TRUE );
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
                if (!isRecordExists) {

                    UploadObject emptyFolderRequest = new UploadObject();
                    emptyFolderRequest.setObjectName( user.get().getUserName() + "/" );
                    emptyFolderRequest.setContent( "" );
                    emptyFolderRequest.setOwner( user.get().getUserName() );
                    emptyFolderRequest.setOwnerId( user.get().getUserId() );
                    emptyFolderRequest.setBucketName( bucket.get().getBucketName() );
                    Boolean emptyFolderCreated = coreAPIService.createEmptyFolder( emptyFolderRequest );
                    bucketAggregate.addBucketAccessingUsers( bucketAccessRequest.getUserId(), readAndWriteAccess.getBucketAccessId() );
                    if (emptyFolderCreated) {
                        bucketAggregateRepository.save( bucketAggregate );
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                }

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
        if (accessObject.isPresent() && Boolean.TRUE.equals( accessObject.get().getRead() ))
            return approveReadBucketAccessRequest( bucketAccessRequest );
        return approveWriteBucketAccessRequest( bucketAccessRequest );
    }


    @Transactional
    public Boolean approveBucketAccessRequest(BucketAccessRequestDto bucketAccessRequestDto) {
        log.info( "Inside approveBucketAccessRequest" );
        try {
            Optional<BucketAccessRequestEntity> bucketAccessRequest = this.bucketAccessRequestEntityRepository.findById( bucketAccessRequestDto.getId() );
            if (bucketAccessRequest.isPresent() && Boolean.TRUE.equals( approveByRequestType( bucketAccessRequest.get().approve() ) )) {
                bucketAccessRequestEntityRepository.save( bucketAccessRequest.get() );
                return Boolean.TRUE;
            }

        } catch (Exception e) {
            log.error( "Exception while approving the Bucket Access Request" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public Boolean rejectBucketAccessRequest(BucketAccessRequestDto bucketAccessRequestDto) {
        log.info( "Inside rejectBucketAccessRequest" );
        try {
            Optional<BucketAccessRequestEntity> bucketAccessRequest = this.bucketAccessRequestEntityRepository.findById( bucketAccessRequestDto.getId() );
            if (bucketAccessRequest.isPresent()) {
                bucketAccessRequest.get().reject();
                bucketAccessRequestEntityRepository.save( bucketAccessRequest.get() );
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error( "Exception while rejecting the Bucket Access Request" + e.getMessage() );
        }
        return Boolean.FALSE;
    }


    public Boolean addUserToBucketByBucketAdmin(UserBucketMapping addUserFromUiToBucket) {
        log.info( "Inside addUserToBucketByBucketAdmin" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( addUserFromUiToBucket.getBucketName() ) );
            Optional<UserAggregate> user = Objects.requireNonNull( userAggregateRepository.findById( addUserFromUiToBucket.getUserId() ) );
            if (user.isPresent()) {
                BucketAccessEntity readAndWriteAccess = Objects.requireNonNull( bucketAccessEntityRepository.findByReadAndWrite( true, true ) );
                bucket.addBucketAccessingUsers( user.get().getUserId(), readAndWriteAccess.getBucketAccessId() );
                UploadObject emptyFolderRequest = new UploadObject();
                emptyFolderRequest.setObjectName( user.get().getUserName() + "/" );
                emptyFolderRequest.setContent( "" );
                emptyFolderRequest.setOwner( user.get().getUserName() );
                emptyFolderRequest.setOwnerId( user.get().getUserId() );
                emptyFolderRequest.setBucketName( bucket.getBucketName() );
                Boolean emptyFolderCreated = coreAPIService.createEmptyFolder( emptyFolderRequest );
                if (emptyFolderCreated) {
                    bucketAggregateRepository.save( bucket );
                    return Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            log.error( "Exception while adding user to the Bucket by admin" + e.getMessage() );
        }
        return Boolean.FALSE;
    }

    public Boolean addUsersToBucketByBucketAdmin(List<UserBucketMapping> addUsersFromUiToBucket) {
        List<Boolean> results = addUsersFromUiToBucket.stream()
                .map( this::addUserToBucketByBucketAdmin )
                .collect( Collectors.toList() );
        return !results.contains( false );
    }


    public Status removeUserFromBucketByBucketAdmin(UserBucketMapping removeUserFromBucket) {
        log.info( "Inside removeUserToBucketByBucketAdmin" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( removeUserFromBucket.getBucketName() ) );
            Optional<UserAggregate> userExists = Objects.requireNonNull( userAggregateRepository.findById( removeUserFromBucket.getUserId() ) );
            if (userExists.isPresent()) {
                UserAggregate user = userExists.get();
                Boolean userExistsInBucket = bucket.isUserExistsInBucket( user.getUserId() );

                int bucketObjectsCount = bucket.getBucketObjects( user.getUserId() );

                if (userExistsInBucket && bucketObjectsCount <= 1) {

                    final Boolean removedAccessingUser = bucket.removeBucketAccessingUsers( user.getUserId() );

                    Boolean emptyFolderDeleted = coreAPIService.deleteObject( user.getUserName() + "/", bucket.getBucketName(), user.getUserId() );
                    if (removedAccessingUser && emptyFolderDeleted) bucketAggregateRepository.save( bucket );
                    status.setValue( Boolean.TRUE );
                } else {
                    status.setValue( Boolean.FALSE );
                    status.setReasonForFailure( "Either User not exists or user has more Bucket Objects which has to be deleted before removing the user" );
                }
            }
        } catch (Exception e) {
            log.error( "Exception while removing user from the Bucket by admin" + e );
            status.setValue( Boolean.FALSE );
        }

        return status;
    }


    private BucketAccessRequestDto bucketAccessRequestDtoMapper(BucketAccessRequestEntity bucketAccessRequestEntity) {
        BucketAccessRequestDto bucketAccessRequestDto = new BucketAccessRequestDto();
        bucketAccessRequestDto.setId( bucketAccessRequestEntity.getId() );
        userAggregateRepository.findById( bucketAccessRequestEntity.getUserId() )
                .ifPresent( userAggregate -> bucketAccessRequestDto.setUserName( userAggregate.getUserName() ) );
        bucketAggregateRepository.findById( bucketAccessRequestEntity.getBucketId() )
                .ifPresent( bucketAggregate -> bucketAccessRequestDto.setBucketName( bucketAggregate.getBucketName() ) );
        bucketAccessEntityRepository.findById( bucketAccessRequestEntity.getBucketAccessId() )
                .ifPresent( bucketAccessEntity -> bucketAccessRequestDto.setBucketAccessType( bucketAccessEntity.getAccessInfo() ) );
        bucketAccessRequestDto.setStatus( bucketAccessRequestEntity.getStatus() );
        return bucketAccessRequestDto;
    }

    public List<BucketAccessRequestDto> getBucketAccessRequestForAdmin() {
        log.info( "Inside getBucketAccessRequestForAdmin" );
        return bucketAccessRequestEntityRepository.findAll().stream()
                .map( this::bucketAccessRequestDtoMapper ).collect( Collectors.toList() );
    }

    public List<BucketAccessRequestEntity> getBucketAccessRequestForUser(String userName) {
        return bucketAccessRequestEntityRepository.findAllByUserId( userAggregateRepository.findByUserName( userName ).getUserId() );
    }
}
