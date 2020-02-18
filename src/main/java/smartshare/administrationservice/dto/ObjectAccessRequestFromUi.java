package smartshare.administrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data
class ObjectAccessRequestFromUi {

    private String userName;
    private String bucketName;
    private String objectName;
    private String ownerName;
    private String access;

}
