package smartshare.administrationservice.models;


import lombok.Data;
import smartshare.administrationservice.constant.StatusConstants;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public @Data
class BucketObjectAccessRequestEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int ownerId;
    private int userId;
    private int bucketId;
    private int bucketObjectId;
    private int objectAccessId;
    private String status = StatusConstants.INPROGRESS.toString();

    public BucketObjectAccessRequestEntity approve() {
        this.status = StatusConstants.APPROVED.toString();
        return this;
    }

    public BucketObjectAccessRequestEntity reject() {
        this.status = StatusConstants.REJECTED.toString();
        return this;
    }

}
