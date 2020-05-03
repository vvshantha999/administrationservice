package smartshare.administrationservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public @Data
class ObjectAccessEntity {

    @Id
    private int objectAccessId;
    private Boolean read = Boolean.FALSE;
    private Boolean write = Boolean.FALSE;
    private Boolean delete = Boolean.FALSE;

    public List<Boolean> toList() {
        List<Boolean> access = new ArrayList<>();
        access.add( this.getRead() );
        access.add( this.getWrite() );
        access.add( this.getDelete() );
        return access;
    }
    public String getAccessInfo() {
        StringBuilder accessBuilder = new StringBuilder();
        if (Boolean.TRUE.equals( read )) accessBuilder.append( "Read" );
        if (Boolean.TRUE.equals( write )) accessBuilder.append( "Write" );
        if (Boolean.TRUE.equals( delete )) accessBuilder.append( "Delete" );
        return accessBuilder.toString().trim();
    }


}
