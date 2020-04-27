package smartshare.administrationservice.models;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@NoArgsConstructor
public class BucketObjectAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bucketObjectId;
    private String bucketObjectName;

    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private BucketAggregate bucket;

    private int ownerId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bucketObject", orphanRemoval = true)
    private Set<BucketObjectAccessingUser> accessingUsers = new HashSet<>();

    public BucketObjectAggregate(String bucketObjectName, BucketAggregate bucket, int ownerId) {
        this.bucketObjectName = bucketObjectName;
        this.bucket = bucket;
        this.ownerId = ownerId;
        this.addAccessingUser( ownerId, 8 );
    }


    public BucketObjectAggregate addAccessingUser(int userId, int objectAccessId) {
        final BucketObjectAccessingUser newBucketObjectAccessingUser = new BucketObjectAccessingUser( this.bucket.getBucketId(), this, userId, objectAccessId );
        this.accessingUsers.add( newBucketObjectAccessingUser );
        newBucketObjectAccessingUser.setBucketObject( this );
        return this;
    }

    public Boolean removeAccessingUser(BucketObjectAccessingUser accessingUser) {
        accessingUser.setBucketObject( null );
        this.accessingUsers.remove( accessingUser );
        return true;
    }

    public Boolean isUserExistsInBucketObject(int userId) {
        return this.getAccessingUsers().stream()
                .anyMatch( bucketObjectAccessingUser -> bucketObjectAccessingUser.getUserId() == userId );
    }

    public int getBucketObjectId() {
        return bucketObjectId;
    }

    public void setBucketObjectId(int bucketObjectId) {
        this.bucketObjectId = bucketObjectId;
    }

    public String getBucketObjectName() {
        return bucketObjectName;
    }

    public void setBucketObjectName(String bucketObjectName) {
        this.bucketObjectName = bucketObjectName;
    }

    public BucketAggregate getBucket() {
        return bucket;
    }

    public void setBucket(BucketAggregate bucket) {
        this.bucket = bucket;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Set<BucketObjectAccessingUser> getAccessingUsers() {
        return accessingUsers;
    }

    public void setAccessingUsers(Set<BucketObjectAccessingUser> accessingUsers) {
        this.accessingUsers = accessingUsers;
    }
}
