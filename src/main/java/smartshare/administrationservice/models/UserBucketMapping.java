package smartshare.administrationservice.models;

import javax.persistence.*;

@Entity
public class UserBucketMapping implements Cloneable {


    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;
    @OneToOne
    @JoinColumn(name = "access_id", referencedColumnName = "Id")
    private BucketAccess access;

    public UserBucketMapping(User user, Bucket bucket, BucketAccess bucketAccess) {
        this.user = user;
        this.bucket = bucket;
        this.access = bucketAccess;
    }

    public User getUser() {
        return user;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public BucketAccess getAccess() {
        return access;
    }

    public void setAccess(BucketAccess access) {
        this.access = access;
    }

    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
