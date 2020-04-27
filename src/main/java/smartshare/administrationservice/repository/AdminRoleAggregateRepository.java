package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.AdminRoleAggregate;

@Repository
public interface AdminRoleAggregateRepository extends JpaRepository<AdminRoleAggregate, String> {
}
