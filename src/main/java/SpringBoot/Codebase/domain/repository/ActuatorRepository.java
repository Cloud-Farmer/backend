package SpringBoot.Codebase.domain.repository;

import SpringBoot.Codebase.domain.entity.Actuator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActuatorRepository extends JpaRepository<Actuator, Long> {

}
