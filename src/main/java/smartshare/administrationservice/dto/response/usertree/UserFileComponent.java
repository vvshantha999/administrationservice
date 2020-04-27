package smartshare.administrationservice.dto.response.usertree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public @Data
class UserFileComponent extends UserBucketComponent {

    public UserFileComponent(String name) {
        this.name = name;

    }


}
