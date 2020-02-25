package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.AdminRoleAggregate;

import java.util.Optional;

@Repository
public interface AdminRoleAggregateRepository extends JpaRepository<AdminRoleAggregate, Integer> {
    Optional<AdminRoleAggregate> findFirstByOrderByAdminIdDesc();
}
