package smartshare.administrationservice.dto;

import lombok.Data;
import smartshare.administrationservice.constant.StatusConstants;

import java.util.List;


public @Data
class SagaEvent {

    private String eventId;
    private List<BucketObjectEvent> objects;
    private String status = StatusConstants.INPROGRESS.toString();

}
