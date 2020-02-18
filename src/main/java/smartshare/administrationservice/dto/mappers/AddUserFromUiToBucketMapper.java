package smartshare.administrationservice.dto.mappers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.AddUserFromUiToBucket;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.ObjectAccessRepository;
import smartshare.administrationservice.repository.UserRepository;

import java.util.Collections;



@Component
public class AddUserFromUiToBucketMapper implements Mapper {

    private BucketRepository bucketRepository;
    private UserRepository userRepository;
    private ObjectAccessRepository objectAccessRepository;



    @Autowired
    AddUserFromUiToBucketMapper(BucketRepository bucketRepository, UserRepository userRepository, ObjectAccessRepository objectAccessRepository) {
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
        this.objectAccessRepository = objectAccessRepository;
    }


    @Override
    public <T, U> T map(U objectToBeTransformed) {

        AddUserFromUiToBucket addUserFromUiToBucket = (AddUserFromUiToBucket) objectToBeTransformed;
        Bucket bucketToWhichUserWillBeAdded = bucketRepository.findByName( addUserFromUiToBucket.getBucketName() );
        User userToBeAdded = userRepository.findByUserName( addUserFromUiToBucket.getUserName() );
        if (null != bucketToWhichUserWillBeAdded && null != userToBeAdded) {
            BucketObject newBucketObjectForTheUserBeingAdded =
                    new BucketObject( addUserFromUiToBucket.getObjectName(), bucketToWhichUserWillBeAdded, userToBeAdded );
            AccessingUser newAccessingUserEntryForTheNewBucketObjectBeingAdded =
                    new AccessingUser( userToBeAdded, newBucketObjectForTheUserBeingAdded,
                            objectAccessRepository.findByReadAndWriteAndDelete( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE ) );
            newBucketObjectForTheUserBeingAdded.setAccessingUsers( Collections.singletonList( newAccessingUserEntryForTheNewBucketObjectBeingAdded ) );
            bucketToWhichUserWillBeAdded.setObjects( Collections.singletonList( newBucketObjectForTheUserBeingAdded ) );
            bucketToWhichUserWillBeAdded.setAccessingUsers( Collections.singletonList( new UserBucketMapping( userToBeAdded, bucketToWhichUserWillBeAdded, new BucketAccess( Boolean.TRUE, Boolean.TRUE ) ) ) );
            return (T) bucketToWhichUserWillBeAdded;
        }

        return null;
    }

}
