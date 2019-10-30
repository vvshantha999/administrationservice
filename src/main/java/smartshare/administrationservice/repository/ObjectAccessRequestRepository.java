package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartshare.administrationservice.models.ObjectAccessRequest;

public interface ObjectAccessRequestRepository extends JpaRepository<ObjectAccessRequest, Long> {
}
