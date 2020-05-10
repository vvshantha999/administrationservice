package smartshare.administrationservice.dto.response.ownertree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public @Data
class FolderComponent extends BucketComponent {

    @JsonIgnore
    private FolderComponent parent;
    @JsonProperty(value = "children")
    List<BucketComponent> bucketComponents = new ArrayList<>();

    public FolderComponent(String name, String completeName, String owner, FolderComponent parent) {
        this.name = name;
        this.completeName = completeName;
        this.owner = owner;
        this.parent = parent;
    }

    public void addAll(List<FileComponent> file) {
        bucketComponents.addAll( file );
    }

    public BucketComponent add(FolderComponent file) {
        bucketComponents.add( file );
        return file;
    }
}
