package smartshare.administrationservice.dto.response.ownertree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public @Data
class FileComponent extends BucketComponent {

    private String user;
    private String accessInfo;

    public FileComponent(String name, String owner, String user, String accessInfo) {
        this.name = name;
        this.owner = owner;
        this.user = user;
        this.accessInfo = accessInfo;
    }


}
