package smartshare.administrationservice.dto;

import lombok.Data;

public @Data
class UserAccessingObject {
    private String userName;
    private String accessInfo;

    public UserAccessingObject(String userName, String accessInfo) {
        this.userName = userName;
        this.accessInfo = accessInfo;
    }
}
