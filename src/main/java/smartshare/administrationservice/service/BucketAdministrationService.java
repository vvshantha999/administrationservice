package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.Status;
import smartshare.administrationservice.models.AdminRoleAggregate;
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.repository.AdminRoleAggregateRepository;
import smartshare.administrationservice.repository.BucketAggregateRepository;

import java.util.Optional;

@Slf4j
@Service
public class BucketAdministrationService {


    private Status status;
    private AdminRoleAggregateRepository adminRoleAggregateRepository;
    private BucketAggregateRepository bucketAggregateRepository;

    @Autowired
    BucketAdministrationService(
            Status status,
            AdminRoleAggregateRepository adminRoleAggregateRepository,
            BucketAggregateRepository bucketAggregateRepository
    ) {
        this.status = status;
        this.adminRoleAggregateRepository = adminRoleAggregateRepository;
        this.bucketAggregateRepository = bucketAggregateRepository;
    }


    public BucketAggregate createBucket(String bucketName) {
        log.info( "Inside createBucket" );
        BucketAggregate newBucket = new BucketAggregate();
        newBucket.setBucketName( bucketName );
        // assuming update happens getting the current admin object
        Optional<AdminRoleAggregate> adminRoleExists = adminRoleAggregateRepository.findFirstByOrderByAdminIdDesc();
        System.out.println( "adminRoleExists------>" + adminRoleExists );
        if (adminRoleExists.isPresent()) newBucket.setAdminId( adminRoleExists.get().getAdminId() );
        else throw new IllegalArgumentException( "Admin Role is not Assigned" );
        return bucketAggregateRepository.save( newBucket );
    }

    public Status deleteBucket(String bucketName) {
        log.info( "Inside deleteBucket" );

        BucketAggregate bucketToBeDeleted = bucketAggregateRepository.findByBucketName( bucketName );
        if (bucketToBeDeleted.getBucketObjects().isEmpty()) {
            bucketAggregateRepository.delete( bucketToBeDeleted );
            status.setValue( Boolean.TRUE );
        } else {
            status.setValue( Boolean.FALSE );
            status.setReasonForFailure( "Please delete Bucket Objects before deleting the bucket" );
        }
        return status;
    }


    //    @KafkaListener(groupId = "accessManagementBucketConsumer", topics = "AccessManagement")
    public void consume(String bucket, ConsumerRecord record) {

        try {
            switch (record.key().toString()) {
                case "createBucket":
                    log.info( "Consumed createBucket Event" );
                    System.out.println();
                    BucketAggregate bucketInAccessManagementDb = this.createBucket( bucket );
                    log.info( "Bucket " + bucketInAccessManagementDb.getBucketName() + " Info is added in the Access Management " );
                    break;
                case "deleteBucket":
                    log.info( "Consumed deleteBucket Event" );
                    Status status = this.deleteBucket( bucket );
                    log.info( "Bucket Info delete event Handling result in the Access Management Server " + status.getValue() + " Reason If Failed: " + status.getReasonForFailure() );
                    break;
                default:
                    log.error( "Unsupported accessManagementBucketConsumer event" );
            }
        } catch (Exception e) {
            log.error( "Exception while handling the accessManagementBucketConsumer events " + e.getMessage() );
        }
    }

}
