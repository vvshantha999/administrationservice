package smartshare.administrationservice.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class BucketAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bucketId;
    private String bucketName;
    private int adminId;
    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BucketObjectAggregate> bucketObjects = new HashSet<>();

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
        this.bucketObjects.add( new BucketObjectAggregate( bucketObjectName, this, ownerId ) );
        return this;
    }

    public Boolean removeBucketObject(String bucketObjectName, int ownerId) {
        return this.bucketObjects.removeIf( bucketObjectAggregate -> bucketObjectAggregate.getBucketObjectName().equals( bucketObjectName ) && bucketObjectAggregate.getOwnerId() == ownerId );
    }

    public boolean removeUsersRootBucketObject(int ownerId) {
        return this.bucketObjects.removeIf( bucketObjectAggregate -> bucketObjectAggregate.getOwnerId() == ownerId );
    }

    public BucketAggregate addBucketAccessingUsers(int userId, int bucketAccessId) {
        this.bucketAccessingUsers.add( new BucketAccessingUser( this, userId, bucketAccessId ) );
        return this;
    }

    public boolean removeBucketAccessingUsers(int userId) {
        return this.bucketAccessingUsers.removeIf( bucketAccessingUser -> bucketAccessingUser.getUserId() == userId );
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

//    public BucketObjectAggregate findBucketObjectByBucketObjectId(int id){
//        for (BucketObjectAggregate bucketObjectAggregate : this.getBucketObjects()) {
//            System.out.println("bucketObjectAggregate --- >"+bucketObjectAggregate.getBucketObjectId());
//            if(bucketObjectAggregate.getBucketObjectId() == id) return bucketObjectAggregate;
//        }
//        return null;
//    }


}
