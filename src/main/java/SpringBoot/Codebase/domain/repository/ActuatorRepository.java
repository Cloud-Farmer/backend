package SpringBoot.Codebase.domain.repository;

import SpringBoot.Codebase.domain.entity.Actuator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActuatorRepository extends JpaRepository<Actuator, Long> {
    @Query(value = "select a from Actuator a where a.kitId = ?1 and a.sensor = ?2 order by a.time desc")
    public List<Actuator> findByKitIdAndSensorOrderByTimeDesc(Long kitId, String sensor, Pageable pageable);

}
