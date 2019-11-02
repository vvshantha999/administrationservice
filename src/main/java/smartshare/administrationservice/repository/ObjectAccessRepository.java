package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.ObjectAccess;

@Repository
public interface ObjectAccessRepository extends JpaRepository<ObjectAccess, Long> {

    ObjectAccess findByReadAndWriteAndDelete(Boolean read, Boolean write, Boolean delete);

}
