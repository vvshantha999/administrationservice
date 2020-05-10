package smartshare.administrationservice.dto.response.usertree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data
class UserFolderComponent extends UserBucketComponent {

    @JsonIgnore
    private UserFolderComponent parent;
    @JsonProperty(value = "children")
    List<UserBucketComponent> userBucketComponents = new ArrayList<>();

    public UserFolderComponent(String name, String completeName, UserFolderComponent parent) {
        this.name = name;
        this.completeName = completeName;
        this.parent = parent;
    }

    public void addAll(List<UserFileComponent> file) {
        userBucketComponents.addAll( file );
    }

    public UserBucketComponent add(UserBucketComponent file) {
        userBucketComponents.add( file );
        return file;
    }
}
