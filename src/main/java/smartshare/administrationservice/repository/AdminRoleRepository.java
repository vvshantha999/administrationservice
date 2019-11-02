package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.AdminRole;

@Repository
public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {
    AdminRole findFirstByOrderByAdminAccessDesc();// if new inserts are happening instead of update

}
