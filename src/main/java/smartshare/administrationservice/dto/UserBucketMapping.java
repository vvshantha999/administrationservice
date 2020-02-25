package smartshare.administrationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data
class UserBucketMapping {

    private String userName;
    private String bucketName;

    public UserBucketMapping(String userName, String bucketName) {
        this.userName = userName;
        this.bucketName = bucketName;
        // this.objectName = this.bucketName + "/" + this.userName + "/";
    }

}
