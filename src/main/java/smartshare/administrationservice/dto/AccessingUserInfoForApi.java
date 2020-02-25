package smartshare.administrationservice.dto;

import lombok.Data;
import smartshare.administrationservice.models.ObjectAccessEntity;

public @Data
class AccessingUserInfoForApi {

    private String userName;
    private Boolean read;
    private Boolean write;
    private Boolean delete;

    public AccessingUserInfoForApi(String userName, ObjectAccessEntity access) {
        this.userName = userName;
        this.read = access.getRead();
        this.write = access.getWrite();
        this.delete = access.getDelete();
    }

}
