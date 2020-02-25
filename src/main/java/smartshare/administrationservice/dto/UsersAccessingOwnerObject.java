package smartshare.administrationservice.dto;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
public @Data
class UsersAccessingOwnerObject {

    @JsonProperty("objectName")
    private String objectName;
    @JsonProperty("accessingUsers")
    private List<UserAccessingObject> accessingUsers;


}
