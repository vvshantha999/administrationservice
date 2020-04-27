package smartshare.administrationservice.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
public @Data
class AdminAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;
    private int userId;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
}
