package smartshare.administrationservice.dto;

import smartshare.administrationservice.models.ObjectAccessRequest;

import java.util.List;

public class ObjectAccessRequestForFolders {

    private List<ObjectAccessRequest> objectAccessRequestsForFolder;

    public List<ObjectAccessRequest> getObjectAccessRequestsForFolder() {
        return objectAccessRequestsForFolder;
    }

    @Override
    public String toString() {
        return "ObjectAccessRequestForFolders{" +
                "objectAccessRequestsForFolder=" + objectAccessRequestsForFolder +
                '}';
    }
}
