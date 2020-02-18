package smartshare.administrationservice.models;

import lombok.Data;
import smartshare.administrationservice.constant.StatusConstants;

import javax.persistence.*;

@Entity
public @Data
class BucketAccessRequest {

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


    public void approve() {
        this.status = StatusConstants.APPROVED.toString();
    }

    public void reject() {
        this.status = StatusConstants.REJECTED.toString();
    }

}
