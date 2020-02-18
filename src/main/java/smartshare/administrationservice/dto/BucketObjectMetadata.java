package smartshare.administrationservice.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BucketObjectMetadata {

    private String objectName;
    private ObjectMetadata objectMetadata;

    public BucketObjectMetadata(String objectName, ObjectMetadata objectMetadata) {
        this.objectName = objectName;
        this.objectMetadata = objectMetadata;
    }


    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    @Override
    public String toString() {
        return "BucketObjectMetadata{" +
                "objectName='" + objectName + '\'' +
                ", objectMetadata=" + objectMetadata +
                '}';
    }
}
