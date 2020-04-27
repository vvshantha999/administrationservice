package smartshare.administrationservice.dto.response.usertree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data
class UserFolderComponent extends UserBucketComponent {

    @JsonProperty(value = "children")
    List<UserBucketComponent> userBucketComponents = new ArrayList<>();

    public UserFolderComponent(String name) {
        this.name = name;
    }

    public void addAll(List<UserFileComponent> file) {
        userBucketComponents.addAll( file );
    }

    public UserBucketComponent add(UserBucketComponent file) {
        userBucketComponents.add( file );
        return file;
    }
}
