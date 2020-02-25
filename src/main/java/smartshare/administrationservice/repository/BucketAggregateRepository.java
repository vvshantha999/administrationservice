package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketAggregate;

@Repository
public interface BucketAggregateRepository extends JpaRepository<BucketAggregate, Integer> {
    BucketAggregate findByBucketName(String bucketName);
}
