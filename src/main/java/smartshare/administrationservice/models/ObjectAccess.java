package smartshare.administrationservice.models;

import javax.persistence.Entity;

@Entity
public class ObjectAccess extends BucketAccess {

    private Boolean delete;


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
