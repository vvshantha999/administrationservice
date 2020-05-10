package smartshare.administrationservice.dto;

import lombok.Data;
import smartshare.administrationservice.dto.response.ownertree.AccessInfo;

public @Data
class UploadObject {
    String objectName;
    String content;
    String owner;
    int ownerId;
    String bucketName;
    AccessInfo defaultAccessInfo = new AccessInfo( Boolean.TRUE, Boolean.TRUE, Boolean.TRUE );

}

