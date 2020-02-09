package smartshare.administrationservice.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RemoveUserFromBucket extends AddUserFromUiToBucket {
    public RemoveUserFromBucket(String userName, String bucketName) {
        super( userName, bucketName );
    }
}
