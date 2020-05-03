package smartshare.administrationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private int ownerId;
    private int userId;
    private String status = StatusConstants.INPROGRESS.toString();


    @JsonCreator
    public BucketObjectEvent(
            @JsonProperty("bucketName") String bucketName,
            @JsonProperty("objectName") String objectName,
            @JsonProperty("ownerName") String ownerName,
            @JsonProperty("userName") String userName,
            @JsonProperty("status") String status,
            @JsonProperty("ownerId") int ownerId,
            @JsonProperty("userId") int userId
    ) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.ownerName = ownerName;
        this.userName = userName;
        this.status = status;
        this.ownerId = ownerId;
        this.userId = userId;
    }

    public BucketObjectEvent duplicate() {
        BucketObjectEvent duplicateBucketObjectEvent = new BucketObjectEvent();
        duplicateBucketObjectEvent.bucketName = this.getBucketName();
        duplicateBucketObjectEvent.objectName = this.getObjectName();
        duplicateBucketObjectEvent.ownerName = this.getOwnerName();
        duplicateBucketObjectEvent.userName = this.getUserName();
        return duplicateBucketObjectEvent;
    }

}
