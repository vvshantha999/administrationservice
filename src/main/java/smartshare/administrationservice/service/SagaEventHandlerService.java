package smartshare.administrationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import smartshare.administrationservice.dto.BucketObjectEvent;
import smartshare.administrationservice.dto.SagaEvent;

import java.util.List;

@Slf4j
@Service
public class SagaEventHandlerService {


    private BucketObjectAdministrationService bucketObjectAdministrationService;

    @Autowired
    public SagaEventHandlerService(BucketObjectAdministrationService bucketObjectAdministrationService) {
        this.bucketObjectAdministrationService = bucketObjectAdministrationService;
    }


    private SagaEvent createEventHandler(SagaEvent sagaEvent) {
        log.info( "Inside createEventHandler" );
        SagaEvent sagaEventResult = new SagaEvent();
        sagaEventResult.setEventId( sagaEvent.getEventId() );
        List<BucketObjectEvent> bucketObjectEventResults = bucketObjectAdministrationService.createAccessDetailForUploadedBucketObjects( sagaEvent.getObjects() );
        sagaEventResult.setObjects( bucketObjectEventResults );
//        sagaEventResult.setObjects( sagaEvent.getObjects() ); testing..
        return sagaEventResult;
    }

    private SagaEvent deleteEventHandler(SagaEvent sagaEvent) {
        log.info( "Inside deleteEventHandler" );
        SagaEvent sagaEventResult = new SagaEvent();
        sagaEventResult.setEventId( sagaEvent.getEventId() );
        List<BucketObjectEvent> bucketObjectEventResults = bucketObjectAdministrationService.deleteBucketObjects( sagaEvent.getObjects() );
        sagaEventResult.setObjects( bucketObjectEventResults );
        return sagaEventResult;
    }

    // Testing

//    @KafkaListener(groupId = "sagaEventResultConsumer",topics = "sagaAccessResult")
//    public void result(SagaEvent results) {
//        log.info( "Inside sagaEventResultConsumer" );
//        System.out.println( results);
//    }


    @KafkaListener(groupId = "sagaEventConsumer", topics = "sagaAccess", containerFactory = "SagaEventKafkaListenerContainerFactory")
    @SendTo("sagaAccessResult")
    public SagaEvent consume(SagaEvent sagaEvent, ConsumerRecord record) {

        try {

            switch (record.key().toString()) {
                case "create":
                    log.info( "Consumed create saga Events" );
                    return this.createEventHandler( sagaEvent );
                case "delete":
                    log.info( "Consumed delete saga Events" );
                    return this.deleteEventHandler( sagaEvent );
                default:
                    log.error( "Unsupported accessManagementBucketConsumer event" );
            }
        } catch (Exception e) {
            log.error( "Exception while handling the accessManagementBucketConsumer events " + e.getMessage() );
        }

        return sagaEvent;
    }
}
