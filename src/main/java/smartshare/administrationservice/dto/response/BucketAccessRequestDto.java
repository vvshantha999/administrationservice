package smartshare.administrationservice.dto.response;

import lombok.Data;

public @Data
class BucketAccessRequestDto {

    private String userName;
    private String bucketName;
    private String bucketAccessType;
    private String status;
}
