package io.aud.authenticationservice.repositories;

import io.aud.authenticationservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
