package com.mikedevcol.auth_service.services;

import org.springframework.stereotype.Service;

import com.mikedevcol.auth_service.models.Role;
import com.mikedevcol.auth_service.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public Role findByName(String name) {
    return roleRepository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Role not found: " + name));
  }

}
