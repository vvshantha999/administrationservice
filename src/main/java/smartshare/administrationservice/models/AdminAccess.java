package smartshare.administrationservice.models;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "admin_access_given")
public @Data
class AdminAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private Date createdOn;

}
