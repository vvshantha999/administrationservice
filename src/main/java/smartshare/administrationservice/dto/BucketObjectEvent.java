package smartshare.administrationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import smartshare.administrationservice.constant.StatusConstants;

@NoArgsConstructor
public @Data
class BucketObjectEvent {

    private String bucketName;
    private String objectName;
    private String ownerName;
    private String userName;
    private String status = StatusConstants.INPROGRESS.toString();


    public BucketObjectEvent duplicate() {
        BucketObjectEvent duplicateBucketObjectEvent = new BucketObjectEvent();
        duplicateBucketObjectEvent.bucketName = this.getBucketName();
        duplicateBucketObjectEvent.objectName = this.getObjectName();
        duplicateBucketObjectEvent.ownerName = this.getOwnerName();
        duplicateBucketObjectEvent.userName = this.getUserName();
        return duplicateBucketObjectEvent;
    }

}
