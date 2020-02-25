package smartshare.administrationservice.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public @Data
class AdminRoleAggregate {

    @Id
    private String adminRoleId = UUID.fromString( "5fc03087-d265-11e7-b8c6-83e29cd24f4c" ).toString();
    private int adminId;
}
