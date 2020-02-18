package smartshare.administrationservice.models;


import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ObjectAccess extends BucketAccess {

    private Boolean delete = false;

    public ObjectAccess() {
        super();

    }

    public ObjectAccess(String access) {
        super( access );
        if (access.equals( "delete" )) this.delete = Boolean.TRUE;

    }

    public ObjectAccess(Boolean read, Boolean write, Boolean delete) {
        super( read, write );
        this.delete = delete;
    }


    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public List<Boolean> toList() {
        List<Boolean> currentAccess = new ArrayList<>();
        currentAccess.add( getRead() );
        currentAccess.add( getWrite() );
        currentAccess.add( getDelete() );
        return currentAccess;
    }

    @Override
    public String toString() {
        return "ObjectAccess{" +
                "id=" + this.getId() +
                ", read=" + this.getRead() +
                ", write=" + this.getWrite() +
                ", delete=" + delete +
                '}';
    }
}
