package smartshare.administrationservice.dto;


import lombok.Data;
import smartshare.administrationservice.models.BucketAccessEntity;

public @Data
class BucketMetadata {

    private String bucketName;
    private Boolean read;
    private Boolean write;


    public BucketMetadata(String bucketName, BucketAccessEntity access) {
        this.bucketName = bucketName;
        this.read = access.getRead();
        this.write = access.getWrite();
    }


}
