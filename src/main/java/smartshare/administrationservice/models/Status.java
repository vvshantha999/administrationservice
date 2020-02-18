package smartshare.administrationservice.models;

import lombok.Data;

public @Data
class Status {

    private Boolean value;
    private String reasonForFailure;

}
