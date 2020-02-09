package smartshare.administrationservice.dto.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.ObjectAccessRepository;
import smartshare.administrationservice.repository.UserRepository;

import java.util.Optional;

@Component
public class AddUserFromUiToBucketMapper implements Mapper {

    private BucketRepository bucketRepository;
    private UserRepository userRepository;
    private ObjectAccessRepository objectAccessRepository;

    private Optional<Bucket> bucketToWhichUserIsAdded;
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
        bucketToWhichUserIsAdded = Optional.ofNullable( bucketRepository.findByName( addUserFromUiToBucket.getBucketName() ) );
        Optional<User> user = Optional.ofNullable( userRepository.findByUserName( addUserFromUiToBucket.getUserName() ) );
        if (bucketToWhichUserIsAdded.isPresent() && user.isPresent()) {
            BucketObject userObjectsCollectionMetadataToBeCreated = new BucketObject( addUserFromUiToBucket.getObjectName(), bucketToWhichUserIsAdded.get(), user.get() );
            userObjectsCollectionMetadataToBeCreated.addAccessingUser( new AccessingUser( user.get(), userObjectsCollectionMetadataToBeCreated, objectAccessRepository.findByReadAndWriteAndDelete( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE ) ) );
            bucketToWhichUserIsAdded.get().addBucketObject( userObjectsCollectionMetadataToBeCreated );
            newUserMapping = new UserBucketMapping( user.get(), bucketToWhichUserIsAdded.get(), new BucketAccess( Boolean.TRUE, Boolean.TRUE ) );
        }

        return (T) this;
    }

    public Bucket getBucketToWhichUserIsAdded() {
        return bucketToWhichUserIsAdded.orElse( null );
    }

    public UserBucketMapping getNewUserMapping() {
        return newUserMapping;
    }
}
