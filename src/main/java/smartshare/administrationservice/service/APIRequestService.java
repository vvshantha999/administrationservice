package smartshare.administrationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.*;
import smartshare.administrationservice.dto.response.UserLoginStatus;
import smartshare.administrationservice.dto.response.UserMetadata;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.*;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class APIRequestService {


    private final BucketAggregateRepository bucketAggregateRepository;
    private final UserAggregateRepository userAggregateRepository;
    private final ObjectAccessEntityRepository objectAccessEntityRepository;
    private final BucketAccessEntityRepository bucketAccessEntityRepository;
    private final AdminRoleAggregateRepository adminRoleAggregateRepository;
    private final AdminAggregateRepository adminAggregateRepository;
    private final BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository;


    @Autowired
    public APIRequestService(

            BucketAggregateRepository bucketAggregateRepository,
            UserAggregateRepository userAggregateRepository,
            ObjectAccessEntityRepository objectAccessEntityRepository,
            BucketAccessEntityRepository bucketAccessEntityRepository,
            AdminRoleAggregateRepository adminRoleAggregateRepository,
            AdminAggregateRepository adminAggregateRepository,
            BucketAccessRequestEntityRepository bucketAccessRequestEntityRepository) {
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.userAggregateRepository = userAggregateRepository;
        this.objectAccessEntityRepository = objectAccessEntityRepository;
        this.bucketAccessEntityRepository = bucketAccessEntityRepository;
        this.adminRoleAggregateRepository = adminRoleAggregateRepository;
        this.adminAggregateRepository = adminAggregateRepository;
        this.bucketAccessRequestEntityRepository = bucketAccessRequestEntityRepository;
    }


    private BucketObjectMetadata mapToReadModel(BucketObjectAggregate bucketObject, UserAggregate user) {
        log.info( "Inside mapToReadModel" );
        BucketObjectMetadata bucketObjectMetadata = null;
        Optional<UserAggregate> owner = userAggregateRepository.findById( bucketObject.getOwnerId() );
        Optional<BucketObjectAccessingUser> accessingUser = bucketObject.getAccessingUsers().stream()
                .filter( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() == user.getUserId() )
                .findFirst();
        if (accessingUser.isPresent() && owner.isPresent()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setOwnerName( owner.get().getUserName() );
            ObjectAccessEntity accessInfo = objectAccessEntityRepository.findById( accessingUser.get().getObjectAccessId() )
                    .orElseGet( () -> objectAccessEntityRepository.findByReadAndWriteAndDelete( false, false, false ) );
            objectMetadata.setAccessingUserInfo( new AccessingUserInfoForApi( user.getUserName(), accessInfo ) );
            bucketObjectMetadata = new BucketObjectMetadata( bucketObject.getBucketObjectName(), objectMetadata );
        }
        return bucketObjectMetadata;
    }


    public List<BucketObjectMetadata> fetchBucketObjectsMetaDataByBucketNameAndUserName(String bucketName, String userName) {
        log.info( "Inside fetchBucketObjectsMetaDataByBucketAndUser" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketName ) );
            UserAggregate user = Objects.requireNonNull( userAggregateRepository.findByUserName( userName ) );
            return bucket.getBucketObjects().stream()
                    .map( bucketObjectAggregate -> mapToReadModel( bucketObjectAggregate, user ) )
                    .filter( Objects::nonNull )
                    .sorted( Comparator.comparing( BucketObjectMetadata::getObjectName ) )
                    .collect( Collectors.toList() );
        } catch (Exception e) {
            log.error( "Exception inside fetchBucketObjectsMetaDataByBucketAndUser service layer ", e );
        }
        return Collections.emptyList();
    }


    private BucketMetadata extractBucketMetadata(BucketAggregate bucket, int userId) {
        log.info( "Inside extractBucketMetadata" );
        final BucketAccessEntity defaultAccessInfo = bucketAccessEntityRepository.findByReadAndWrite( false, false );
        Optional<BucketMetadata> bucketMetadata = bucket.getBucketAccessingUsers().stream()
                .filter( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId )
                .findFirst()
                .flatMap( bucketAccessingUser -> bucketAccessEntityRepository.findById( bucketAccessingUser.getBucketAccessId() ) )
                .map( bucketAccessEntity -> new BucketMetadata( bucket.getBucketName(), bucketAccessEntity.getRead(), bucketAccessEntity.getWrite() ) );

        return bucketMetadata.orElseGet( () -> new BucketMetadata( bucket.getBucketName(), defaultAccessInfo.getRead(), defaultAccessInfo.getWrite() ) );
    }

    public List<BucketMetadata> fetchBucketsMetaDataByUserName(String userName, String email) {
        log.info( "Inside fetchMetaDataForBucketsInS3" );
        try {
            Optional<UserAggregate> user = userAggregateRepository.findByUserNameAndEmail( userName, email );
            if (user.isPresent()) {
                return bucketAggregateRepository.findAll().stream()
                        .map( bucket -> extractBucketMetadata( bucket, user.get().getUserId() ) )
                        .collect( Collectors.toList() );
            }
        } catch (Exception e) {
            log.error( "Exception inside fetchMetaDataForBucketsInS3  ", e );
        }
        return Collections.emptyList();
    }


    public boolean createAdmin(UserDto user) {

        log.info( "Inside createAdmin" );
        try {
            Optional<UserAggregate> userExists = this.userAggregateRepository.findByUserNameAndEmail( user.getUserName(), user.getEmail() );
            if (userExists.isPresent()) {
                System.out.println( "inside" );
                AdminAggregate newAdmin = new AdminAggregate();
                newAdmin.setUserId( userExists.get().getUserId() );
                newAdmin.setCreatedOn( new Date() );
                final AdminAggregate newlyCreatedAdmin = adminAggregateRepository.save( newAdmin );
                AdminRoleAggregate assignNewAdminWithAdminRole = new AdminRoleAggregate();
                assignNewAdminWithAdminRole.setAdminId( newlyCreatedAdmin.getAdminId() );
                adminRoleAggregateRepository.save( assignNewAdminWithAdminRole );
                return true;
            }
        } catch (Exception e) {
            log.error( "Exception while creating admin" + e.getMessage() );
        }
        return false;
    }

    private boolean isAdmin(UserAggregate user) {
        log.info( "Inside isAdmin" );
        final Optional<AdminRoleAggregate> adminRoleAggregate = adminRoleAggregateRepository.findById( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );
        if (adminRoleAggregate.isPresent()) {
            final Optional<AdminAggregate> adminAggregate = this.adminAggregateRepository.findById( adminRoleAggregate.get().getAdminId() );
            if (adminAggregate.isPresent()) {
                return adminAggregate.get().getUserId() == user.getUserId();
            }
        }
        return false;
    }

    public UserLoginStatus registerUserAndCheckIsAdmin(UserDto user) {
        log.info( "Inside registerUserAndCheckIsAdmin" );
        try {
            Optional<UserAggregate> userExists = this.userAggregateRepository.findByUserNameAndEmail( user.getUserName(), user.getEmail() );
            if (userExists.isPresent()) {
                return new UserLoginStatus( userExists.get(), isAdmin( userExists.get() ) );
            } else {
                UserAggregate newUser = new UserAggregate();
                newUser.setUserName( user.getUserName() );
                newUser.setEmail( user.getEmail() );
                final UserAggregate savedUser = userAggregateRepository.save( newUser );
                return new UserLoginStatus( savedUser, false );
            }

        } catch (Exception e) {
            log.error( "Exception  in registerUserAndCheckIsAdmin " + e.getMessage() );
        }
        return null;
    }

    public List<UserAggregate> getUsers() {
        log.info( "Inside getUsers" );
        try {
            return userAggregateRepository.findAll();
        } catch (Exception e) {
            log.error( "Exception  in getUsers " + e.getMessage() );
        }
        return Collections.emptyList();
    }

    private int getBucketRequestCount(int userId) {
        return (int) bucketAccessRequestEntityRepository.findAllByUserId( userId ).stream()
                .filter( bucketAccessRequestEntity -> bucketAccessRequestEntity.getStatus().equals( StatusConstants.INPROGRESS.toString() ) )
                .count();
    }


    public List<UserMetadata> getUsersMetadata() {
        log.info( "Inside getUsersMetadata" );

        return userAggregateRepository.findAll().stream()
                .map( userAggregate -> {
                    UserMetadata userMetadata = new UserMetadata();
                    userMetadata.setName( userAggregate.getUserName() );
                    userMetadata.setEmail( userAggregate.getEmail() );
                    boolean isAdmin = isAdmin( userAggregate );
                    userMetadata.setAdmin( isAdmin );
                    bucketAggregateRepository.findAll().forEach( bucketAggregate -> {
                        final Optional<BucketAccessingUser> isAccessingUserExist = bucketAggregate.getBucketAccessingUsers().stream()
                                .filter( bucketAccessingUser -> bucketAccessingUser.getUserId() == userAggregate.getUserId() )
                                .findFirst();
                        if (isAccessingUserExist.isPresent()) {
                            List<String> updatedBucketNamesList = userMetadata.getBucketNames();
                            updatedBucketNamesList.add( bucketAggregate.getBucketName() );
                            userMetadata.setBucketNames( updatedBucketNamesList );
                        }

                    } );
                    userMetadata.setBucketRequestsCount( getBucketRequestCount( userAggregate.getUserId() ) );
                    return userMetadata;
                } )
                .collect( Collectors.toList() );
    }

}
