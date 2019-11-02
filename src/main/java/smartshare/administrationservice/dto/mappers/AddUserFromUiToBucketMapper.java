package smartshare.administrationservice.dto.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.ObjectAccessRepository;
import smartshare.administrationservice.repository.UserRepository;

@Component
public class AddUserFromUiToBucketMapper implements Mapper {

    private BucketRepository bucketRepository;
    private UserRepository userRepository;
    private ObjectAccessRepository objectAccessRepository;

    private Bucket bucketToWhichUserIsAdded;
    private UserBucketMapping newUserMapping;


    @Autowired
    AddUserFromUiToBucketMapper(BucketRepository bucketRepository, UserRepository userRepository, ObjectAccessRepository objectAccessRepository) {
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
        this.objectAccessRepository = objectAccessRepository;
    }


    @Override
    public <T, U> T map(U objectToBeTransformed) {
        AddUserFromUiToBucket addUserFromUiToBucket = (AddUserFromUiToBucket) objectToBeTransformed;
        bucketToWhichUserIsAdded = bucketRepository.findByName( addUserFromUiToBucket.getBucketName() );
        User user = userRepository.findByUserName( addUserFromUiToBucket.getUserName() );
        BucketObject userObjectsCollectionMetadataToBeCreated = new BucketObject( addUserFromUiToBucket.getObjectName(), bucketToWhichUserIsAdded, user );
        userObjectsCollectionMetadataToBeCreated.addAccessingUser( new AccessingUser( user, userObjectsCollectionMetadataToBeCreated, objectAccessRepository.findByReadAndWriteAndDelete( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE ) ) );
        bucketToWhichUserIsAdded.addBucketObject( userObjectsCollectionMetadataToBeCreated );
        newUserMapping = new UserBucketMapping( user, bucketToWhichUserIsAdded, new BucketAccess( Boolean.TRUE, Boolean.TRUE ) );
        return (T) this;
    }

    public Bucket getBucketToWhichUserIsAdded() {
        return bucketToWhichUserIsAdded;
    }

    public UserBucketMapping getNewUserMapping() {
        return newUserMapping;
    }
}
