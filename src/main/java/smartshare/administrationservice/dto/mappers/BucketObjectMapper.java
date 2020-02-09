package smartshare.administrationservice.dto.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.BucketObjectFromApi;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.UserRepository;


@Component
public class BucketObjectMapper implements Mapper {

    private BucketRepository bucketRepository;
    private UserRepository userRepository;

    @Autowired
    BucketObjectMapper(BucketRepository bucketRepository, UserRepository userRepository) {
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public <T, U> T map(U objectToBeTransformed) {
        BucketObjectFromApi bucketObjectFromApi = (BucketObjectFromApi) objectToBeTransformed;
        User bucketObjectOwner = userRepository.findByUserName( bucketObjectFromApi.getOwnerName() );
        Bucket bucket = bucketRepository.findByName( bucketObjectFromApi.getBucketName() );
        BucketObject bucketObject = new BucketObject( bucketObjectFromApi.getObjectName(), bucket, bucketObjectOwner );
        AccessingUser newAccessingUser = new AccessingUser( bucketObjectOwner,
                bucketObject, new ObjectAccess( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE ) );
        bucketObject.addAccessingUser( newAccessingUser );
        return (T) bucketObject;
    }
}
