package org.brain.springtelegramai.service.impl;


import org.brain.springtelegramai.model.RoleEntity;
import org.brain.springtelegramai.repository.RoleRepository;
import org.brain.springtelegramai.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public record RoleServiceImpl(RoleRepository roleRepository) implements RoleService {
    private static final String USER_ROLE = "ROLE_USER";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    @Override
    public RoleEntity userRole(){
        return roleRepository.findByName(USER_ROLE).orElse(roleRepository.save(RoleEntity.builder().name(USER_ROLE).build()));
    }

    @Override
    public RoleEntity adminRole(){
        return roleRepository.findByName(ADMIN_ROLE).orElse(roleRepository.save(RoleEntity.builder().name(ADMIN_ROLE).build()));
    }
}
