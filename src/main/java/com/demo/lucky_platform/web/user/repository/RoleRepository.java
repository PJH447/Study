package com.demo.lucky_platform.web.user.repository;

import com.demo.lucky_platform.web.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(String authority);
}
