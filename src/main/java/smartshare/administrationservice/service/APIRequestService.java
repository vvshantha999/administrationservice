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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile( "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE );


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
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Optional<UserAggregate> owner = userAggregateRepository.findById( bucketObject.getOwnerId() );
        Optional<BucketObjectAccessingUser> accessingUser = bucketObject.getAccessingUsers().stream()
                .filter( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() == user.getUserId() )
                .findFirst();
        if (owner.isPresent()) {
            objectMetadata.setOwnerName( owner.get().getUserName() );
            objectMetadata.setOwnerId( owner.get().getUserId() );
        }
        if (accessingUser.isPresent()) {
            ObjectAccessEntity accessInfo = objectAccessEntityRepository.findById( accessingUser.get().getObjectAccessId() )
                    .orElseGet( () -> objectAccessEntityRepository.findByReadAndWriteAndDelete( false, false, false ) );
            objectMetadata.setAccessingUserInfo( new AccessingUserInfoForApi( user.getUserName(), accessInfo ) );
        }
        return new BucketObjectMetadata( bucketObject.getBucketObjectName(), objectMetadata );
    }

    public static boolean validateUserNameAsEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher( emailStr );
        return matcher.find();
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

    public List<BucketObjectMetadata> fetchBucketObjectsMetaDataByBucketNameAndUserId(String bucketName, int userId) {
        log.info( "Inside fetchBucketObjectsMetaDataByBucketNameAndUserId" );
        try {
            BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( bucketName ) );
            Optional<UserAggregate> user = userAggregateRepository.findById( userId );
            if (user.isPresent() && !bucket.getBucketObjects().isEmpty())
                return bucket.getBucketObjects().stream()
                        .map( bucketObjectAggregate -> mapToReadModel( bucketObjectAggregate, user.get() ) )
                        .filter( Objects::nonNull )
                        .sorted( Comparator.comparing( BucketObjectMetadata::getObjectName ) )
                        .collect( Collectors.toList() );
        } catch (Exception e) {
            log.error( "Exception inside fetchBucketObjectsMetaDataByBucketAndUser service layer ", e );
        }
        return Collections.emptyList();
    }

    public List<BucketMetadata> fetchBucketsMetaDataByUserId(int userId) {
        log.info( "Inside fetchMetaDataForBucketsInS3" );
        try {
            Optional<UserAggregate> user = userAggregateRepository.findById( userId );
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

    public boolean createAdmin(int userId) {

        log.info( "Inside createAdmin" );
        try {
            Optional<UserAggregate> userExists = this.userAggregateRepository.findById( userId );
            if (userExists.isPresent()) {

                AdminAggregate newAdmin = new AdminAggregate();
                newAdmin.setUserId( userId );
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

    private boolean isDefaultAdmin() {
        return adminRoleAggregateRepository.findAll().isEmpty() && userAggregateRepository.findAll().size() == 1;
    }

    private boolean isAdmin(UserAggregate user) {
        log.info( "Inside isAdmin" );
        // initial setup first user admin by default
        final Optional<AdminRoleAggregate> adminRoleAggregate = adminRoleAggregateRepository.findById( UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString() );
        if (adminRoleAggregate.isPresent()) {
            final Optional<AdminAggregate> adminAggregate = this.adminAggregateRepository.findById( adminRoleAggregate.get().getAdminId() );
            if (adminAggregate.isPresent()) {
                return adminAggregate.get().getUserId() == user.getUserId();
            }
        }
        return false;
    }

    public String generateUniqueUserName(UserDto user) {
        return (validateUserNameAsEmail( user.getUserName() )) ?
                user.getUserName().split( "@" )[0] + "_" + user.getEmail().split( "@" )[0] :
                user.getUserName() + "_" + user.getEmail().split( "@" )[0];
    }

    public UserLoginStatus registerUserAndCheckIsAdmin(UserDto user) {
        log.info( "Inside registerUserAndCheckIsAdmin" );
        try {
            UserAggregate userExists = this.userAggregateRepository.findByUserName( generateUniqueUserName( user ) );
            if (userExists != null) {
                return new UserLoginStatus( userExists, isAdmin( userExists ), isDefaultAdmin() );
            } else {
                UserAggregate newUser = new UserAggregate();
                newUser.setUserName( generateUniqueUserName( user ) );
                newUser.setEmail( user.getEmail() );
                final UserAggregate savedUser = userAggregateRepository.save( newUser );
                return new UserLoginStatus( savedUser, false, isDefaultAdmin() );
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
                    userMetadata.setUserId( userAggregate.getUserId() );
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

    public Boolean doesAccessExist(int userId, String bucketName, String accessType) {
        log.info( "Inside getUserBucketMetadata" );
        try {
            BucketAggregate bucket = bucketAggregateRepository.findByBucketName( bucketName );
            boolean isUserExists = bucket.getBucketAccessingUsers().stream()
                    .anyMatch( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId );
            if (isUserExists) {
                final Optional<BucketAccessingUser> accessingUser = bucket.getBucketAccessingUsers().stream()
                        .filter( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId )
                        .findAny();
                if (accessingUser.isPresent()) {
                    Optional<BucketAccessEntity> access = bucketAccessEntityRepository.findById( accessingUser.get().getBucketAccessId() );
                    if (access.isPresent() && accessType.equals( "read" )) return access.get().getRead();
                    if (access.isPresent() && accessType.equals( "write" )) return access.get().getWrite();
                }
            }
        } catch (Exception e) {
            log.error( "Exception while doesAccessExist " + e.getMessage() );
        }
        return false;
    }
}
