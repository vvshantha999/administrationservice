package smartshare.administrationservice.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
public class Bucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
    private AdminRole adminRole;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL) // have to verify mapping
    private List<BucketObject> objects = new ArrayList<>();

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL)
    private List<UserBucketMapping> accessingUsers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdminRole getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }

    public List<BucketObject> getObjects() {
        return objects;
    }

    public void setObjects(List<BucketObject> objects) {
        this.objects = objects;
    }

    public List<UserBucketMapping> getAccessingUsers() {
        return accessingUsers;
    }

    public void setAccessingUsers(List<UserBucketMapping> accessingUsers) {
        this.accessingUsers = accessingUsers;
    }

    public Bucket addUser(UserBucketMapping userBucketMapping) {
        this.accessingUsers.add( userBucketMapping );
        return this;
    }

    public Bucket removeUser(UserBucketMapping userBucketMapping) {
        this.accessingUsers = this.accessingUsers.stream()
                .filter( userBucketMapping1 -> !userBucketMapping1.equals( userBucketMapping ) )
                .collect( Collectors.toList() );
        return this;
    }

    public void addBucketObject(BucketObject bucketObject) {
        this.objects.add( bucketObject );
    }

    public void removeBucketObject(BucketObject bucketObject) {
        this.getObjects().remove( bucketObject );
    }


}
