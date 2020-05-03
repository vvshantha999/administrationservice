package smartshare.administrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data
class ObjectAccessRequest {

    private String userName;
    private String bucketName;
    private String objectName;
    private int ownerId;
    private String access;

}

