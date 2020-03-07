package smartshare.administrationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import smartshare.administrationservice.dto.BucketObjectMetadata;

import java.util.List;

@AllArgsConstructor
public @Data
class BucketObjectsMetadata {

    private List<BucketObjectMetadata> bucketObjectsMetadata;
}
