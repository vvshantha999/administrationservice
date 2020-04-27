package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.BucketObjectEvent;

import java.util.Arrays;


@Slf4j
@Service
public class BucketObjectEventAdministrationService {


    private BucketObjectAdministrationService bucketObjectAdministrationService;

    @Autowired
    public BucketObjectEventAdministrationService(BucketObjectAdministrationService bucketObjectAdministrationService) {
        this.bucketObjectAdministrationService = bucketObjectAdministrationService;
    }


    @KafkaListener(groupId = "accessManagementObjectConsumer", topics = "BucketObjectAccessManagement", containerFactory = "kafkaListenerContainerFactory")
    public void consume(BucketObjectEvent[] bucketObjects, ConsumerRecord record) {

        try {

            switch (record.key().toString()) {
                case "emptyBucketObject":
                    log.info( "Consumed emptyBucketObject Event" );
                    bucketObjectAdministrationService.createAccessDetailForBucketObject( bucketObjects[0] );
                    break;
                case "uploadBucketObjects":
                    log.info( "Consumed uploadBucketObjects Event" );
                    bucketObjectAdministrationService.createAccessDetailForUploadedBucketObjects( Arrays.asList( bucketObjects ) );
                    break;
                case "deleteBucketObjects":
                    log.info( "Consumed deleteBucketObjects Event" );
                    bucketObjectAdministrationService.deleteBucketObjects( Arrays.asList( bucketObjects ) );
                    break;
                default:
                    log.error( "Unsupported accessManagementBucketConsumer event" );
            }
        } catch (Exception e) {
            log.error( "Exception while handling the accessManagementBucketConsumer events " + e.getMessage() );
        }
    }


}
