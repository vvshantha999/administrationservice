package smartshare.administrationservice.dto.response.ownertree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data
class FolderComponent extends BucketComponent {

    @JsonProperty(value = "children")
    List<BucketComponent> bucketComponents = new ArrayList<>();

    public FolderComponent(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public void addAll(List<FileComponent> file) {
        bucketComponents.addAll( file );
    }

    public BucketComponent add(FolderComponent file) {
        bucketComponents.add( file );
        return file;
    }
}
