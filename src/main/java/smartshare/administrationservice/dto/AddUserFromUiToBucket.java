package smartshare.administrationservice.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AddUserFromUiToBucket {

    private String userName;
    private String bucketName;
    private String objectName;

    public AddUserFromUiToBucket(String userName, String bucketName) {
        this.userName = userName;
        this.bucketName = bucketName;
        this.objectName = this.bucketName + "/" + this.userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    @Override
    public String toString() {
        return "addUserFromUi{" +
                "userName='" + userName + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", objectName='" + objectName + '\'' +
                '}';
    }
}
