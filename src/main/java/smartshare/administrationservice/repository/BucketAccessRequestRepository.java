package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.BucketAccessRequest;
import smartshare.administrationservice.models.User;

import java.util.List;

@Repository
public interface BucketAccessRequestRepository extends JpaRepository<BucketAccessRequest, Long> {

    List<BucketAccessRequest> findAllByUser(User user);
}
