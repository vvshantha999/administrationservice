package smartshare.administrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public @Data
class BucketAccessRequestFromUi {

    private int userId;
    private String bucketName;
    private String access;

}
