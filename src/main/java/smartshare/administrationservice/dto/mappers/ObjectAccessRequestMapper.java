package smartshare.administrationservice.dto.mappers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.models.ObjectAccess;
import smartshare.administrationservice.models.ObjectAccessRequest;
import smartshare.administrationservice.repository.BucketObjectRepository;
import smartshare.administrationservice.repository.UserRepository;


@Component
public class ObjectAccessRequestMapper implements Mapper {

    private BucketObjectRepository bucketObjectRepository;
    private UserRepository userRepository;


    @Autowired
    ObjectAccessRequestMapper(BucketObjectRepository bucketObjectRepository, UserRepository userRepository) {
        this.bucketObjectRepository = bucketObjectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public <T, U> T map(U objectToBeTransformed) {
        ObjectAccessRequestFromUi objectAccessRequestFromUi = (ObjectAccessRequestFromUi) objectToBeTransformed;
        ObjectAccessRequest objectAccessRequest = new ObjectAccessRequest();
        objectAccessRequest.setBucketObject( bucketObjectRepository.findByName( objectAccessRequestFromUi.getObjectName() ) );
        objectAccessRequest.setOwner( userRepository.findByUserName( objectAccessRequestFromUi.getOwnerName() ) );
        objectAccessRequest.setUser( userRepository.findByUserName( objectAccessRequestFromUi.getUserName() ) );
        objectAccessRequest.setAccess( new ObjectAccess( objectAccessRequestFromUi.getAccess() ) );
        return (T) objectAccessRequest;
    }
}
