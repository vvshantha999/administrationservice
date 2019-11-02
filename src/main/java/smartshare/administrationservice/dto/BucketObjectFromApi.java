package smartshare.administrationservice.dto;

public class BucketObjectFromApi {

    private String bucketName;
    private String objectName;
    private String ownerName;
    private String userName;

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getUserName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return "BucketObjectFromApi{" +
                "bucketName='" + bucketName + '\'' +
                ", objectName='" + objectName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", userName='" + ownerName + '\'' +
                '}';
    }
}
