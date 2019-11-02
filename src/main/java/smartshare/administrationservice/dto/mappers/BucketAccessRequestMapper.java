package smartshare.administrationservice.dto.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.constant.StatusConstants;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.models.BucketAccess;
import smartshare.administrationservice.models.BucketAccessRequest;
import smartshare.administrationservice.repository.AdminRoleRepository;
import smartshare.administrationservice.repository.BucketRepository;
import smartshare.administrationservice.repository.UserRepository;

@Component
public class BucketAccessRequestMapper implements Mapper {

    private BucketRepository bucketRepository;
    private UserRepository userRepository;
    private AdminRoleRepository adminRoleRepository;

    @Autowired
    BucketAccessRequestMapper(BucketRepository bucketRepository, UserRepository userRepository, AdminRoleRepository adminRoleRepository) {
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
        this.adminRoleRepository = adminRoleRepository;
    }

    @Override
    public <T, U> T map(U objectToBeTransformed) {
        BucketAccessRequestFromUi bucketAccessRequestFromUi = (BucketAccessRequestFromUi) objectToBeTransformed;
        BucketAccessRequest newBucketAccessRequest = new BucketAccessRequest();
        newBucketAccessRequest.setBucket( bucketRepository.findByName( bucketAccessRequestFromUi.getBucketName() ) );
        newBucketAccessRequest.setAccess( new BucketAccess( bucketAccessRequestFromUi.getAccess() ) );
        newBucketAccessRequest.setUser( userRepository.findByUserName( bucketAccessRequestFromUi.getUserName() ) );
        newBucketAccessRequest.setAdminAccess( adminRoleRepository.getOne( Long.valueOf( "0000" ) ).getAdminAccess() );
        newBucketAccessRequest.setAdminRole( adminRoleRepository.getOne( Long.valueOf( "0000" ) ) );
        newBucketAccessRequest.setStatus( StatusConstants.INPROGRESS.toString() );
        return (T) newBucketAccessRequest;
    }
}
