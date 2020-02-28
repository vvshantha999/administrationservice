package smartshare.administrationservice.dto;

import lombok.Data;

import java.util.List;


public @Data
class SagaEvent {

    private String eventId;
    private List<BucketObjectEvent> objects;

}
