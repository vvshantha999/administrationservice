package smartshare.administrationservice.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class AdminRole {

    @Id
    @Column(name = "admin_role_id")
    private Long adminRoleId = Long.valueOf( "0000" );

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private AdminAccess adminAccess;

    @OneToMany(mappedBy = "adminAccess")
    private List<BucketAccessRequest> bucketAccessRequestList;

    public AdminAccess getAdminAccess() {
        return adminAccess;
    }

    public List<BucketAccessRequest> getBucketAccessRequestList() {
        return bucketAccessRequestList;
    }

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    @Override
    public String toString() {
        return "AdminRole{" +
                "adminRoleId=" + adminRoleId +
                ", adminAccess=" + adminAccess +
                ", bucketAccessRequestList=" + bucketAccessRequestList +
                '}';
    }
}
