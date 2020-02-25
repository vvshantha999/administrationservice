package smartshare.administrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartshare.administrationservice.models.ObjectAccessEntity;

@Repository
public interface ObjectAccessEntityRepository extends JpaRepository<ObjectAccessEntity, Integer> {
    ObjectAccessEntity findByReadAndWriteAndDelete(Boolean read, Boolean write, Boolean delete);
}
