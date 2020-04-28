package smartshare.administrationservice.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public @Data
class BucketAccessEntity {

    @Id
    private int bucketAccessId;
    private Boolean read = Boolean.FALSE;
    private Boolean write = Boolean.FALSE;

    public String getAccessInfo() {
        String access = "";
        if (Boolean.TRUE.equals( read )) access = "Read";
        if (Boolean.TRUE.equals( write )) access = "Write";
        return access;
    }
}
