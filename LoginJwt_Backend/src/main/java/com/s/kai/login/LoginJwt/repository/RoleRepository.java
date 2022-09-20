package com.s.kai.login.LoginJwt.repository;

import com.s.kai.login.LoginJwt.models.ERole;
import com.s.kai.login.LoginJwt.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
