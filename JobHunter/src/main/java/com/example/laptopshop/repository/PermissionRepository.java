package com.example.laptopshop.repository;

import com.example.laptopshop.domain.Permission;
import com.example.laptopshop.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Page<Permission> findAll(Specification<Permission> spec, Pageable pageable);
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
    boolean existsById(Long id);
    List<Permission> findByIdIn(List<Long> id);
    boolean existsByName(String name);
}
