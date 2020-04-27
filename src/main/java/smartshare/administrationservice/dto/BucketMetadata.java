package smartshare.administrationservice.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data
class BucketMetadata {

    @JsonProperty("bucketName")
    private String bucketName;
    @JsonProperty("read")
    private Boolean read;
    @JsonProperty("write")
    private Boolean write;

    @JsonCreator
    public BucketMetadata(@JsonProperty("bucketName") String bucketName, @JsonProperty("read") Boolean read, @JsonProperty("write") Boolean write) {
        this.bucketName = bucketName;
        this.read = read;
        this.write = write;
    }


}
