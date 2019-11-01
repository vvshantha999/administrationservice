package smartshare.administrationservice.models;

import javax.persistence.*;
import java.util.List;


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
    private List<BucketObject> objects;

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

    @Override
    public String toString() {
        return "Bucket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", adminRole=" + adminRole +
                ", objects=" + objects +
                '}';
    }
}