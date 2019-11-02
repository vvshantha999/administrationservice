package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.Bucket;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long> {

    Bucket findByName(String bucketName);
}
