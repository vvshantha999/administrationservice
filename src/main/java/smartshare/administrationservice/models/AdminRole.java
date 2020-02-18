package smartshare.administrationservice.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
public @Data
class AdminRole {

    @Id
    @Column(name = "admin_role_id")
    private Long adminRoleId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private AdminAccess adminAccess;

    @OneToMany(mappedBy = "adminAccess")
    private List<BucketAccessRequest> bucketAccessRequestList;

}
