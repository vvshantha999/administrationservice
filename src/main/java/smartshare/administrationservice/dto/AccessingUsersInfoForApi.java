package smartshare.administrationservice.dto;

import smartshare.administrationservice.models.ObjectAccess;

public class AccessingUsersInfoForApi {

    private String userName;
    private ObjectAccess access;

    public AccessingUsersInfoForApi(String userName, ObjectAccess access) {
        this.userName = userName;
        this.access = access;
    }

    public String getUserName() {
        return userName;
    }

    public ObjectAccess getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return "AccessingUsersInfoForApi{" +
                "userName='" + userName + '\'' +
                ", access=" + access +
                '}';
    }
}
