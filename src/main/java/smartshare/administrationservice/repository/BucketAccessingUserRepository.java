package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smartshare.administrationservice.models.BucketAccessingUser;

import java.util.Optional;

public interface BucketAccessingUserRepository extends JpaRepository<BucketAccessingUser, Integer> {

    Optional<BucketAccessingUser> findByUserId(int userId);

}
