package smartshare.administrationservice.dto;

import smartshare.administrationservice.models.ObjectAccess;

public class AccessingUserInfoForApi {

    private String userName;
    private Boolean read;
    private Boolean write;
    private Boolean delete;

    public AccessingUserInfoForApi(String userName, ObjectAccess access) {
        this.userName = userName;
        this.read = access.getRead();
        this.write = access.getWrite();
        this.delete = access.getDelete();
    }

    public String getUserName() {
        return userName;
    }

    public Boolean getRead() {
        return read;
    }

    public Boolean getWrite() {
        return write;
    }

    public Boolean getDelete() {
        return delete;
    }

    @Override
    public String toString() {
        return "AccessingUsersInfoForApi{" +
                "userName='" + userName + '\'' +
                ", read=" + read +
                ", write=" + write +
                ", delete=" + delete +
                '}';
    }
}
