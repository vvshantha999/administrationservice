package smartshare.administrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public @Data
class BucketAccessRequestFromUi {

    private String userName;
    private String bucketName;
    private String access;

}
