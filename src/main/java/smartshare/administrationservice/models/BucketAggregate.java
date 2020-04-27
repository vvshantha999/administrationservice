package smartshare.administrationservice.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class BucketAggregate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bucketId;
    @Column(unique = true)
    private String bucketName;
    private int adminId;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<BucketObjectAggregate> bucketObjects = new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bucket", orphanRemoval = true)
    private Set<BucketAccessingUser> bucketAccessingUsers = new HashSet<>();


    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public Set<BucketObjectAggregate> getBucketObjects() {
        return bucketObjects;
    }

    public void setBucketObjects(Set<BucketObjectAggregate> bucketObjects) {
        this.bucketObjects = bucketObjects;
    }

    public Set<BucketAccessingUser> getBucketAccessingUsers() {
        return bucketAccessingUsers;
    }

    public void setBucketAccessingUsers(Set<BucketAccessingUser> bucketAccessingUsers) {
        this.bucketAccessingUsers = bucketAccessingUsers;
    }

    public BucketAggregate addBucketObject(String bucketObjectName, int ownerId) {
        final BucketObjectAggregate newBucketObject = new BucketObjectAggregate( bucketObjectName, this, ownerId );
        this.bucketObjects.add( newBucketObject );
        newBucketObject.setBucket( this );
        return this;
    }

    public Boolean removeBucketObject(String bucketObjectName, int ownerId) {
        Optional<BucketObjectAggregate> bucketObject = this.bucketObjects.stream()
                .filter( bucketObjectAggregate -> bucketObjectAggregate.getBucketObjectName().equals( bucketObjectName ) && bucketObjectAggregate.getOwnerId() == ownerId )
                .findAny();
        if (bucketObject.isPresent()) {
            bucketObject.get().setBucket( null );
            this.bucketObjects.remove( bucketObject.get() );
            // bucketObject.get().setAccessingUsers( null );
            return true;
        }
        return false;
    }

    public boolean removeUsersRootBucketObject(int ownerId) {
        return this.bucketObjects.removeIf( bucketObjectAggregate -> bucketObjectAggregate.getOwnerId() == ownerId );
    }

    public BucketAggregate addBucketAccessingUsers(int userId, int bucketAccessId) {
        BucketAccessingUser bucketAccessingUser = new BucketAccessingUser( this, userId, bucketAccessId );
        this.bucketAccessingUsers.add( bucketAccessingUser );
        bucketAccessingUser.setBucket( this );
        return this;
    }

    public Boolean removeBucketAccessingUsers(int userId) {
        Optional<BucketAccessingUser> user = this.bucketAccessingUsers.stream()
                .filter( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId )
                .findAny();
        if (user.isPresent()) {
            user.get().setBucket( null );
            this.bucketAccessingUsers.remove( user.get() );
            return true;
        }
        return false;
    }

    public Boolean isUserExistsInBucket(int userId) {
        return this.getBucketAccessingUsers().stream()
                .anyMatch( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId );

    }

    public int getBucketObjects(int userId) {
        return (int) this.getBucketObjects().stream()
                .filter( bucketObjectAggregate -> bucketObjectAggregate.getOwnerId() == userId )
                .count();

    }



}
