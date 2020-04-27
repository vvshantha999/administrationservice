package smartshare.administrationservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import smartshare.administrationservice.models.AdminAggregate;


public interface AdminAggregateRepository extends JpaRepository<AdminAggregate, Integer> {

}
