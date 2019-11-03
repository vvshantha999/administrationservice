package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartshare.administrationservice.models.BucketObject;

import java.util.List;

public interface BucketObjectRepository extends JpaRepository<BucketObject, Long> {

    List<BucketObject> findAllByOwner(String owner); // 1st method

    BucketObject findByName(String objectName);

    BucketObject findByNameAndBucket_Name(String name, String bucketName);

}
