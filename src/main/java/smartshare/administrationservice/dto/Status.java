package smartshare.administrationservice.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public @Data
class Status {
    private Boolean value;
    private String reasonForFailure;
}
