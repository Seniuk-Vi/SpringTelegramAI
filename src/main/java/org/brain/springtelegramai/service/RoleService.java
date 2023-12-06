package org.brain.springtelegramai.service;

import org.brain.springtelegramai.model.RoleEntity;

public interface RoleService {
    RoleEntity userRole();

    RoleEntity adminRole();
}
