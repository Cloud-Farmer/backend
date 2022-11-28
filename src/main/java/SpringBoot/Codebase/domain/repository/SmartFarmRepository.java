package SpringBoot.Codebase.domain.repository;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmartFarmRepository extends JpaRepository<SmartFarm, Long> {
}
