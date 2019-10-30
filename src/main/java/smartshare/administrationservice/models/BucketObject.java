package smartshare.administrationservice.models;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


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
    private Set<AccessingUsers> accessingUsers = new HashSet<>();

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

    public Set<AccessingUsers> getAccessingUsers() {
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
