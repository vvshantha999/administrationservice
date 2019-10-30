package smartshare.administrationservice.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
class BucketAccess {

    @Id
    private Long id;
    private Boolean read;
    private Boolean write;

    public Long getId() {
        return id;
    }

    public Boolean getRead() {
        return read;
    }

    public Boolean getWrite() {
        return write;
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
