package smartshare.administrationservice.models;


import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class BucketObjectAccessingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int bucketId;

    @ManyToOne
    @JoinColumn(name = "bucket_object_id")
    private BucketObjectAggregate bucketObject;
    private int userId;
    private int objectAccessId;

    public BucketObjectAccessingUser(int bucketId, BucketObjectAggregate bucketObject, int userId, int objectAccessId) {
        this.bucketId = bucketId;
        this.bucketObject = bucketObject;
        this.userId = userId;
        this.objectAccessId = objectAccessId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public BucketObjectAggregate getBucketObject() {
        return bucketObject;
    }

    public void setBucketObject(BucketObjectAggregate bucketObject) {
        this.bucketObject = bucketObject;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getObjectAccessId() {
        return objectAccessId;
    }

    public void setObjectAccessId(int objectAccessId) {
        this.objectAccessId = objectAccessId;
    }
}
