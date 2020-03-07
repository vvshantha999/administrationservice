package smartshare.administrationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import smartshare.administrationservice.dto.BucketMetadata;

import java.util.List;

@AllArgsConstructor
public @Data
class BucketsMetadata {

    private List<BucketMetadata> bucketsMetadata;
}
