package smartshare.administrationservice.dto.response;

import lombok.Data;

public @Data
class BucketAccessRequestDto {

    private int id;
    private String userName;
    private String bucketName;
    private String bucketAccessType;
    private String status;
}
