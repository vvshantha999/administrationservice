package smartshare.administrationservice.dto.response.ownertree;


import lombok.Data;

public abstract @Data
class BucketComponent {
    protected String name;
    protected String owner;

}
