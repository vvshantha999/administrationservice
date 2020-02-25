package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketObjectAggregate;

@Repository
public interface BucketObjectAggregateRepository extends JpaRepository<BucketObjectAggregate, Integer> {
    BucketObjectAggregate findByBucketObjectIdAndBucket_BucketId(int bucketObjectId, int bucket_bucketId);
}
