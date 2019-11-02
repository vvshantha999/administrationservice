package smartshare.administrationservice.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BucketAccessRequestFromUi {

    private String userName;
    private String bucketName;
    private String access;

    public String getUserName() {
        return userName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return "BucketAccessRequestFromUi{" +
                "userName='" + userName + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", access='" + access + '\'' +
                '}';
    }
}
