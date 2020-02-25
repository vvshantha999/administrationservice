package smartshare.administrationservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Entity
@NoArgsConstructor
public @Data
class AdminAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;
    private int userId;
    @Temporal(TemporalType.DATE)
    private Date createdOn;


    public AdminAggregate(int userId) {
        this.userId = userId;
        this.createdOn = new Date();
    }
}
