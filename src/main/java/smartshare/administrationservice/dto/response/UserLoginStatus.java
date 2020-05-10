package smartshare.administrationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import smartshare.administrationservice.models.UserAggregate;

@AllArgsConstructor
public @Data
class UserLoginStatus {

    private UserAggregate registeredUser;
    private Boolean isAdmin;
    private Boolean defaultAdmin;
}
