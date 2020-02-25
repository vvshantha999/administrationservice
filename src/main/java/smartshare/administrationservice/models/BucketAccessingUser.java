package smartshare.administrationservice.models;


import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class BucketAccessingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private BucketAggregate bucket;
    private int userId;
    private int bucketAccessId;


    public BucketAccessingUser(BucketAggregate bucket, int userId, int bucketAccessId) {
        this.bucket = bucket;
        this.userId = userId;
        this.bucketAccessId = bucketAccessId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BucketAggregate getBucket() {
        return bucket;
    }

    public void setBucket(BucketAggregate bucket) {
        this.bucket = bucket;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBucketAccessId() {
        return bucketAccessId;
    }

    public void setBucketAccessId(int bucketAccessId) {
        this.bucketAccessId = bucketAccessId;
    }
}
