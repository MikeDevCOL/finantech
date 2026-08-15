package com.mikedevcol.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikedevcol.auth_service.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(String name);

}
