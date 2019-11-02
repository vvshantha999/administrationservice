package smartshare.administrationservice.dto;


import java.util.List;

public class ObjectMetadata {

    private String ownerName;
    private List<AccessingUsersInfoForApi> accessingUsersInfo;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<AccessingUsersInfoForApi> getAccessingUsersInfo() {
        return accessingUsersInfo;
    }

    public void setAccessingUsersInfo(List<AccessingUsersInfoForApi> accessingUsersInfo) {
        this.accessingUsersInfo = accessingUsersInfo;
    }

    @Override
    public String toString() {
        return "ObjectMetadata{" +
                "ownerName='" + ownerName + '\'' +
                ", accessingUsersInfo=" + accessingUsersInfo +
                '}';
    }
}
