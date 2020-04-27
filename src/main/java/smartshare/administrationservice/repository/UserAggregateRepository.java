package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.UserAggregate;

import java.util.Optional;

@Repository
public interface UserAggregateRepository extends JpaRepository<UserAggregate, Integer> {
    UserAggregate findByUserName(String userName);

    Optional<UserAggregate> findByUserNameAndEmail(String userName, String email);

    Boolean existsByUserNameAndEmail(String userName, String email);
}
