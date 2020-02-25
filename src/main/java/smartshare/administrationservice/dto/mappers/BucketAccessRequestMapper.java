package smartshare.administrationservice.dto.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.BucketAccessRequestFromUi;
import smartshare.administrationservice.models.AdminRoleAggregate;
import smartshare.administrationservice.models.BucketAccessEntity;
import smartshare.administrationservice.models.BucketAccessRequestEntity;
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.repository.AdminRoleAggregateRepository;
import smartshare.administrationservice.repository.BucketAccessEntityRepository;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.Objects;
import java.util.Optional;


@Component
public class BucketAccessRequestMapper implements Mapper {


    private BucketAggregateRepository bucketAggregateRepository;
    private BucketAccessEntityRepository bucketAccessEntityRepository;
    private UserAggregateRepository userAggregateRepository;
    private AdminRoleAggregateRepository adminRoleAggregateRepository;

    @Autowired
    BucketAccessRequestMapper(
            BucketAggregateRepository bucketAggregateRepository,
            BucketAccessEntityRepository bucketAccessEntityRepository,
            UserAggregateRepository userAggregateRepository,
            AdminRoleAggregateRepository adminRoleAggregateRepository
    ) {
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.bucketAccessEntityRepository = bucketAccessEntityRepository;
        this.userAggregateRepository = userAggregateRepository;
        this.adminRoleAggregateRepository = adminRoleAggregateRepository;
    }


    @Override
    public <T, U> T map(U objectToBeTransformed) {

        BucketAccessRequestFromUi bucketAccessRequestFromUi = (BucketAccessRequestFromUi) objectToBeTransformed;
        BucketAccessRequestEntity newBucketAccessRequest = new BucketAccessRequestEntity();
        BucketAggregate bucketAggregate = bucketAggregateRepository.findByBucketName( bucketAccessRequestFromUi.getBucketName() );
        newBucketAccessRequest.setBucketId( bucketAggregate.getBucketId() );
        BucketAccessEntity bucketAccess = null;
        if (bucketAccessRequestFromUi.getAccess() == "read") {
            bucketAccess = bucketAccessEntityRepository.findByReadAndWrite( true, false );
        }
        if (bucketAccessRequestFromUi.getAccess() == "write") {
            bucketAccess = bucketAccessEntityRepository.findByReadAndWrite( false, true );
        }
        newBucketAccessRequest.setBucketAccessId( Objects.requireNonNull( bucketAccess ).getBucketAccessId() );
        newBucketAccessRequest.setUserId(
                Objects.requireNonNull( userAggregateRepository.findByUserName( bucketAccessRequestFromUi.getUserName() ) ).getUserId()
        );
        Optional<AdminRoleAggregate> adminRoleExists = adminRoleAggregateRepository.findFirstByOrderByAdminIdDesc();
        System.out.println( "adminRoleExists------>" + adminRoleExists );
        adminRoleExists.ifPresent( adminRoleAggregate -> newBucketAccessRequest.setAdminRoleId( adminRoleAggregate.getAdminRoleId() ) );
        return (T) newBucketAccessRequest;
    }
}
