package smartshare.administrationservice.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.List;

@JsonRootName("SagaEvent")
public @Data
class SagaEvent {

    private String eventId;
    private List<BucketObjectEvent> objects;
    private String status;


}
