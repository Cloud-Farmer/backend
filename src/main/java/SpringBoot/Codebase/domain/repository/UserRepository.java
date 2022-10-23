package SpringBoot.Codebase.domain.repository;

import SpringBoot.Codebase.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByFarmname(String farmname);
}
