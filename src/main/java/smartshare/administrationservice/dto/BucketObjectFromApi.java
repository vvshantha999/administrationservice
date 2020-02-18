package smartshare.administrationservice.dto;

import lombok.Data;

public @Data
class BucketObjectFromApi {

    private String bucketName;
    private String objectName;
    private String ownerName;
    private String userName;

}
