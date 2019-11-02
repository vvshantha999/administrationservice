package smartshare.administrationservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartshare.administrationservice.dto.BucketObjectFromApi;
import smartshare.administrationservice.dto.ObjectMetadata;
import smartshare.administrationservice.models.Status;
import smartshare.administrationservice.service.APIRequestService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/", produces = "application/json")
@CrossOrigin(origins = "*")
public class APIRequestController {

    private APIRequestService apiRequestService;

    @Autowired
    APIRequestController(APIRequestService apiRequestService) {
        this.apiRequestService = apiRequestService;
    }

    @GetMapping(value = "objects/accessInfo")
    public List<Map<String, ObjectMetadata>> fetchMetaDataForObjectsInS3() {
        log.info( "Inside fetchMetaDataForObjectsInS3" );
        return apiRequestService.fetchMetaDataForObjectsInS3();
    }

    @DeleteMapping(value = "objects")
    public Status deleteObjectDetails(List<String> objectNamesToBeDeleted) {
        log.info( "Inside deleteObjectDetails" );
        return apiRequestService.deleteGivenObjectsInDb( objectNamesToBeDeleted );
    }

    @PostMapping(value = "objects")
    public Status createAccessDetailsForGivenObject(@RequestBody List<BucketObjectFromApi> bucketObjectsFromApi) {
        log.info( "Inside createAccessDetailsForGivenObject " );
        return apiRequestService.createAccessDetailForGivenBucketObject( bucketObjectsFromApi );// have to confirm how file and folder are saved
    }

}
