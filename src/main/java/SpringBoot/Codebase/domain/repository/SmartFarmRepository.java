package SpringBoot.Codebase.domain.repository;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmartFarmRepository extends JpaRepository<SmartFarm, Long> {
    Optional<SmartFarm> findByMqttAdapterId(String id);
}
