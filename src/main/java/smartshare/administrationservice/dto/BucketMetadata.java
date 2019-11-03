package smartshare.administrationservice.dto;

import smartshare.administrationservice.models.BucketAccess;

public class BucketMetadata {

    private String userName;
    private Boolean read;
    private Boolean write;

    public BucketMetadata(String userName, BucketAccess access) {
        this.userName = userName;
        this.read = access.getRead();
        this.write = access.getWrite();
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
                "userName='" + userName + '\'' +
                ", read=" + read +
                ", write=" + write +
                '}';
    }
}
