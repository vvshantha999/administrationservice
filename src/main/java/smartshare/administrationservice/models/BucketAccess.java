package smartshare.administrationservice.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BucketAccess {


    private Boolean read = Boolean.FALSE;

    @Id
    private Long id;
    private Boolean write = Boolean.FALSE;
    public BucketAccess(String access) {
        switch (access) {
            case "read":
                this.read = Boolean.TRUE;
            case "write":
                this.write = Boolean.TRUE;
        }
    }

    public BucketAccess(Boolean read, Boolean write) {
        this.read = read;
        this.write = write;
    }

    public BucketAccess() {

    }

    public Long getId() {
        return id;
    }

    public Boolean getRead() {
        return read;
    }

    public Boolean getWrite() {
        return write;
    }

    public BucketAccess setRead(Boolean read) {
        this.read = read;
        return this;
    }

    public BucketAccess setWrite(Boolean write) {
        this.write = write;
        return this;
    }

    @Override
    public String toString() {
        return "BucketAccess{" +
                "id=" + id +
                ", read=" + read +
                ", write=" + write +
                '}';
    }
}
