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
}
