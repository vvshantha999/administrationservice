package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketAccessRequestEntity;

import java.util.List;

@Repository
public interface BucketAccessRequestEntityRepository extends JpaRepository<BucketAccessRequestEntity, Integer> {
    List<BucketAccessRequestEntity> findAllByUserId(int userId);
}
