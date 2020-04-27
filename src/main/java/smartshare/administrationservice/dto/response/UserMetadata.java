package smartshare.administrationservice.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data
class UserMetadata {

    // result for users screen
    private String name;
    private String email;
    private boolean admin;
    private int bucketCount;
    private List<String> bucketNames = new ArrayList<>();
    private int bucketRequestsCount;


    public List<String> getBucketNames() {
        return bucketNames;
    }

    public void setBucketNames(List<String> bucketNames) {
        this.bucketNames = bucketNames;
        this.bucketCount = this.bucketNames.size();
    }

}
