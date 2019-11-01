package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartshare.administrationservice.models.ObjectAccessRequest;

import java.util.List;

public interface ObjectAccessRequestRepository extends JpaRepository<ObjectAccessRequest, Long> {

    List<ObjectAccessRequest> findAllByUser(String userName); // 2 method

    List<ObjectAccessRequest> findObjectAccessRequestsByOwnerAndStatus(String ownerName, String status); //3 method


}
