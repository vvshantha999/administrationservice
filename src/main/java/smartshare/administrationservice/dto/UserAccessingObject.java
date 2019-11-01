package smartshare.administrationservice.dto;

public class UserAccessingObject {
    private String userName;
    private String accessInfo;

    public UserAccessingObject(String userName, String accessInfo) {
        this.userName = userName;
        this.accessInfo = accessInfo;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccessInfo() {
        return accessInfo;
    }

    @Override
    public String toString() {
        return "UserAccessingObject{" +
                "userName='" + userName + '\'' +
                ", accessInfo='" + accessInfo + '\'' +
                '}';
    }
}
