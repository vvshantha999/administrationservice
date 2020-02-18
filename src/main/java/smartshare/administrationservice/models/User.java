package smartshare.administrationservice.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String userName;
    private Character adminFlag;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<AccessingUser> accessingUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserBucketMapping> accessingBucket = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private List<ObjectAccessRequest> objectAccessRequestsForOwners;

    @OneToMany(mappedBy = "user")
    private List<ObjectAccessRequest> objectAccessRequestsForUsers;


    @ManyToMany
    @JoinTable(name = "user_bucket_mapping",
            joinColumns = {@JoinColumn(name = "bucket_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<Bucket> buckets = new HashSet<>();


    public User(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Character getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Character adminFlag) {
        this.adminFlag = adminFlag;
    }

    public Set<AccessingUser> getAccessingUsers() {
        return accessingUsers;
    }

    public List<ObjectAccessRequest> getObjectAccessRequestsForOwners() {
        return objectAccessRequestsForOwners;
    }

    public List<ObjectAccessRequest> getObjectAccessRequestsForUsers() {
        return objectAccessRequestsForUsers;
    }

    public Set<Bucket> getBuckets() {
        return buckets;
    }

    public Set<UserBucketMapping> getAccessingBucket() {
        return accessingBucket;
    }


}
