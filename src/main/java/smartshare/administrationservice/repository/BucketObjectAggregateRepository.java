package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketAggregate;
import smartshare.administrationservice.models.BucketObjectAggregate;

import java.util.Optional;

@Repository
public interface BucketObjectAggregateRepository extends JpaRepository<BucketObjectAggregate, Integer> {
    BucketObjectAggregate findByBucketObjectIdAndBucket_BucketId(int bucketObjectId, int bucket_bucketId);

    Optional<BucketObjectAggregate> findByBucketObjectNameAndBucket(String bucketObjectName, BucketAggregate bucket);
}
