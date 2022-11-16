package SpringBoot.Codebase.domain.repository;

import SpringBoot.Codebase.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

}
