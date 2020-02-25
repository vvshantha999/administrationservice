package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketObjectAccessRequestEntity;

import java.util.List;

@Repository
public interface BucketObjectAccessRequestEntityRepository extends JpaRepository<BucketObjectAccessRequestEntity, Integer> {

    List<BucketObjectAccessRequestEntity> findAllByUserId(int userId);

    List<BucketObjectAccessRequestEntity> findAllByOwnerIdAndStatus(int ownerId, String status);
}
