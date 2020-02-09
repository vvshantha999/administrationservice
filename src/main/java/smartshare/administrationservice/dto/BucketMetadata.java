package smartshare.administrationservice.dto;

import smartshare.administrationservice.models.BucketAccess;

public class BucketMetadata {

    private String bucketName;
    private String userName;
    private Boolean read;
    private Boolean write;

    public BucketMetadata(String bucketName, String userName) {
        this.bucketName = bucketName;
        this.userName = userName;
    }

    public BucketMetadata(String bucketName, String userName, BucketAccess access) {
        this( bucketName, userName );
        this.read = access.getRead();
        this.write = access.getWrite();
    }

    public void setAccess(BucketAccess access) {
        this.read = access.getRead();
        this.write = access.getWrite();
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getUserName() {
        return userName;
    }

    public Boolean getRead() {
        return read;
    }

    public Boolean getWrite() {
        return write;
    }

    @Override
    public String toString() {
        return "BucketMetadata{" +
                "bucketName='" + bucketName + '\'' +
                ", userName='" + userName + '\'' +
                ", read=" + read +
                ", write=" + write +
                '}';
    }
}
