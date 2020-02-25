package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.UserAggregate;

@Repository
public interface UserAggregateRepository extends JpaRepository<UserAggregate, Integer> {
    UserAggregate findByUserName(String userName);
}
