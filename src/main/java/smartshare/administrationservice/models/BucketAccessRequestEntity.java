package smartshare.administrationservice.models;

import lombok.Data;
import smartshare.administrationservice.constant.StatusConstants;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public @Data
class BucketAccessRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private int bucketId;
    private int bucketAccessId;
    private String adminRoleId;
    private String status = StatusConstants.INPROGRESS.toString();

    public BucketAccessRequestEntity approve() {
        this.status = StatusConstants.APPROVED.toString();
        return this;
    }

    public BucketAccessRequestEntity reject() {
        this.status = StatusConstants.REJECTED.toString();
        return this;
    }
}
