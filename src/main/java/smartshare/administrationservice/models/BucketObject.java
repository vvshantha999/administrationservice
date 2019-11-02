package smartshare.administrationservice.models;


import javax.persistence.*;
import java.util.List;



@Entity
@Table(name = "OBJECT")
public class BucketObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "Id")
    private User owner;


    @OneToMany(mappedBy = "bucketObject", cascade = CascadeType.ALL)
    private List<AccessingUser> accessingUsers;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public User getOwner() {
        return owner;
    }

    public BucketObject(String name, Bucket bucket, User owner) {
        this.name = name;
        this.bucket = bucket;
        this.owner = owner;
    }

    public BucketObject addAccessingUser(AccessingUser accessingUser) {
        accessingUsers.add( accessingUser );
        return this;
    }

    public List<AccessingUser> getAccessingUsers() {
        return accessingUsers;
    }


    @Override
    public String toString() {
        return "BucketObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bucket=" + bucket +
                ", owner=" + owner +
                ", accessingUsers=" + accessingUsers +
                '}';
    }
}
