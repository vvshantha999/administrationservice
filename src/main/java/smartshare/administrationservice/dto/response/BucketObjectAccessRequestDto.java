package smartshare.administrationservice.dto.response;

import lombok.Data;

public @Data
class BucketObjectAccessRequestDto {

    private String userName;
    private String ownerName;
    private String bucketName;
    private String bucketObjectName;
    private String requestType;
    private String status;
    private int id;
}
