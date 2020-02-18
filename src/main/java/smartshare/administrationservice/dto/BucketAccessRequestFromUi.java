package smartshare.administrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public @Data
class BucketAccessRequestFromUi {

    private String userName;
    private String bucketName;
    private String access;

    @Override
    public String toString() {
        return "BucketAccessRequestFromUi{" +
                "userName='" + userName + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", access='" + access + '\'' +
                '}';
    }
}
