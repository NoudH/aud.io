package io.aud.authenticationservice.repositories;

import io.aud.authenticationservice.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
}
