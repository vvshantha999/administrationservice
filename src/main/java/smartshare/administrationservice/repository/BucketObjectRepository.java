package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartshare.administrationservice.models.BucketObject;

public interface BucketObjectRepository extends JpaRepository<BucketObject, Long> {

}
