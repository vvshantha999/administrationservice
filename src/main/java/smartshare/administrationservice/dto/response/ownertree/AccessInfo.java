package smartshare.administrationservice.dto.response.ownertree;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
public @Data
class AccessInfo implements Serializable {

    private Boolean read;
    private Boolean write;
    private Boolean delete;

}
