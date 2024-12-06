package com.example.laptopshop.service;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.Permission;
import com.example.laptopshop.domain.Role;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.repository.PermissionRepository;
import com.example.laptopshop.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean checkExistById(long id){
        return roleRepository.existsById(id);
    }

    public boolean checkExistByName(String roleName){
        return roleRepository.existsByName(roleName);
    }

    public Role findRoleById(long id){
        Optional<Role> role = roleRepository.findById(id);
        if(role.isPresent()){
            return role.get();
        }
        return null;
    }

    public Role createRole(Role role){
        if(role.getPermissions() != null){
            List<Long> permissionId = role.getPermissions().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Permission> permissions = permissionRepository.findByIdIn(permissionId);
            role.setPermissions(permissions);
        }
        return roleRepository.save(role);
    }

    public Role updateRole(Role role){
        Role updateRole = findRoleById(role.getId());
        if(updateRole != null){
            updateRole.setName(role.getName());
            updateRole.setDescription(role.getDescription());
            List<Long> permissionId = role.getPermissions().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Permission> permissions = permissionRepository.findByIdIn(permissionId);
            updateRole.setPermissions(permissions);
            updateRole.setActive(role.isActive());
            return roleRepository.save(updateRole);
        }
        return null;
    }

    public void deleteRole(long id){
        roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllRoles(Specification<Role> spec, Pageable pageable){
        Page<Role> roles = roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setTotal(roles.getTotalElements());
        meta.setPages(roles.getTotalPages());

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        rs.setMeta(meta);
        rs.setResult(roles.getContent());
        return rs;
    }
}
