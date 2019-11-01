package smartshare.administrationservice.dto;

import smartshare.administrationservice.models.BucketObject;

import java.util.List;

public class UsersAccessingOwnerObject {

    private String objectName;
    private List<UserAccessingObject> accessingUsers;

    public UsersAccessingOwnerObject(BucketObject bucketObject) {
        this.objectName = bucketObject.getName();
        bucketObject.getAccessingUsers().forEach( accessingUser -> {
            StringBuilder accessInfoBuilder = new StringBuilder();
            accessInfoBuilder.append( accessingUser.getAccess().getRead() ? "Read" : "" )
                    .append( " " )
                    .append( accessingUser.getAccess().getWrite() ? "Write" : "" )
                    .append( " " )
                    .append( accessingUser.getAccess().getDelete() ? "Delete" : "" );
            this.add( accessingUser.getUser().getUserName(), accessInfoBuilder.toString().trim() );
        } );

    }

    private void add(String userName, String accessInfo) {
        accessingUsers.add( new UserAccessingObject( userName, accessInfo ) );
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setAccessingUsers(List<UserAccessingObject> accessingUsers) {
        this.accessingUsers = accessingUsers;
    }

    @Override
    public String toString() {
        return "UsersAccessingOwnerObject{" +
                "objectName='" + objectName + '\'' +
                ", accessingUsers=" + accessingUsers +
                '}';
    }
}
