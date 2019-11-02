package smartshare.administrationservice.dto;

public class AddUserFromUiToBucket {

    private String userName;
    private String bucketName;
    private String objectName;

    public String getUserName() {
        return userName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectName() {
        return this.bucketName + "/" + this.userName;
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
