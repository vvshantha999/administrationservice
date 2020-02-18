package smartshare.administrationservice.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public @Data
class UserBucketMapping implements Serializable {


    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;
    @OneToOne
    @JoinColumn(name = "bucket_access_id", referencedColumnName = "Id")
    private BucketAccess access;

    public UserBucketMapping(User user, Bucket bucket, BucketAccess bucketAccess) {
        this.user = user;
        this.bucket = bucket;
        this.access = bucketAccess;
    }

}
