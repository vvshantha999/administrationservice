package smartshare.administrationservice.models;

import smartshare.administrationservice.constant.StatusConstants;

import javax.persistence.*;

@Entity
public class BucketAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private AdminAccess adminAccess;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bucket_id", referencedColumnName = "id")
    private Bucket bucket;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bucket_access_id", referencedColumnName = "id")
    private BucketAccess access;

    private String status;

    @ManyToOne
    @JoinColumn(name = "admin_role_id")
    private AdminRole adminRole;

    public AdminRole getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }

    public AdminAccess getAdminAccess() {
        return adminAccess;
    }

    public void setAdminAccess(AdminAccess adminAccess) {
        this.adminAccess = adminAccess;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public BucketAccess getAccess() {
        return access;
    }

    public void setAccess(BucketAccess access) {
        this.access = access;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void approve() {
        this.status = StatusConstants.APPROVED.toString();
    }

    public void reject() {
        this.status = StatusConstants.REJECTED.toString();
    }

    @Override
    public String toString() {
        return "BucketAccessRequest{" +
                "id=" + id +
                ", adminAccess=" + adminAccess +
                ", user=" + user +
                ", bucket=" + bucket +
                ", access=" + access +
                ", status='" + status + '\'' +
                ", adminRole=" + adminRole +
                '}';
    }
}
