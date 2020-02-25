package smartshare.administrationservice.dto.mappers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import smartshare.administrationservice.dto.ObjectAccessRequestFromUi;
import smartshare.administrationservice.models.*;
import smartshare.administrationservice.repository.BucketAggregateRepository;
import smartshare.administrationservice.repository.ObjectAccessEntityRepository;
import smartshare.administrationservice.repository.UserAggregateRepository;

import java.util.Objects;

@Slf4j
@Component
public class ObjectAccessRequestMapper implements Mapper {


    private UserAggregateRepository userAggregateRepository;
    private BucketAggregateRepository bucketAggregateRepository;
    private ObjectAccessEntityRepository objectAccessEntityRepository;


    @Autowired
    ObjectAccessRequestMapper(
            UserAggregateRepository userAggregateRepository,
            BucketAggregateRepository bucketAggregateRepository,
            ObjectAccessEntityRepository objectAccessEntityRepository
    ) {
        this.userAggregateRepository = userAggregateRepository;
        this.bucketAggregateRepository = bucketAggregateRepository;
        this.objectAccessEntityRepository = objectAccessEntityRepository;
    }


    private ObjectAccessEntity getObjectAccessEntityRecord(String requestedAccess) {
        switch (requestedAccess) {
            case "read":
                return objectAccessEntityRepository.findByReadAndWriteAndDelete( true, false, false );
            case "write":
                return objectAccessEntityRepository.findByReadAndWriteAndDelete( false, true, false );
            case "delete":
                return objectAccessEntityRepository.findByReadAndWriteAndDelete( false, false, true );
            default:
                return objectAccessEntityRepository.findByReadAndWriteAndDelete( false, false, false );
        }
    }

    @Override
    public <T, U> T map(U objectToBeTransformed) {
        ObjectAccessRequestFromUi objectAccessRequestFromUi = (ObjectAccessRequestFromUi) objectToBeTransformed;
        BucketObjectAccessRequestEntity bucketObjectAccessRequest = new BucketObjectAccessRequestEntity();
        UserAggregate user = Objects.requireNonNull( userAggregateRepository.findByUserName( objectAccessRequestFromUi.getUserName() ) );
        UserAggregate owner = Objects.requireNonNull( userAggregateRepository.findByUserName( objectAccessRequestFromUi.getOwnerName() ) );
        BucketAggregate bucket = Objects.requireNonNull( bucketAggregateRepository.findByBucketName( objectAccessRequestFromUi.getBucketName() ) );
        ObjectAccessEntity accessEntity = Objects.requireNonNull( getObjectAccessEntityRecord( objectAccessRequestFromUi.getAccess() ) );
        BucketObjectAggregate bucketObject = null;
        for (BucketObjectAggregate bucketObjectAggregate : bucket.getBucketObjects()) {
            if (bucketObjectAggregate.getBucketObjectName() == objectAccessRequestFromUi.getObjectName())
                bucketObject = bucketObjectAggregate;
        }
        if (null != bucketObject) {
            bucketObjectAccessRequest.setBucketObjectId( bucketObject.getBucketObjectId() );
            bucketObjectAccessRequest.setBucketId( bucket.getBucketId() );
            bucketObjectAccessRequest.setObjectAccessId( accessEntity.getObjectAccessId() );
            bucketObjectAccessRequest.setOwnerId( owner.getUserId() );
            bucketObjectAccessRequest.setUserId( user.getUserId() );
        } else
            log.error( "Bucket Object Not found " + objectAccessRequestFromUi );
        return (T) bucketObjectAccessRequest;
    }
}
