package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketAccessEntity;

@Repository
public interface BucketAccessEntityRepository extends JpaRepository<BucketAccessEntity, Integer> {
    BucketAccessEntity findByReadAndWrite(Boolean read, Boolean write);
}
