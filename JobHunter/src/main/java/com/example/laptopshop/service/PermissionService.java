package com.example.laptopshop.service;

import com.example.laptopshop.domain.Permission;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean checkExistPermission(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(), permission.getMethod());
    }
    public boolean checkExistById(long id) {
        return permissionRepository.existsById(id);
    }
    public boolean isSameName(Permission permission)
    {
        Permission permissionDB = findPermissionById(permission.getId());
        if(permission != null && permissionDB != null){
            if (permissionDB.getName().equals(permission.getName())) {
                return true;
            }
        }
        return false;
    }
    public Permission findPermissionById(Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        if (permission.isPresent()) {
            return permission.get();
        }
        return null;
    }

    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public ResultPaginationDTO getAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setTotal(permissions.getTotalElements());
        meta.setPages(permissions.getTotalPages());

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        rs.setMeta(meta);
        rs.setResult(permissions.getContent());
        return rs;
    }

    public Permission updatePermission(Permission permission) {
        Permission permissionToUpdate = findPermissionById(permission.getId());
        if(permissionToUpdate != null) {
            if(!this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(), permission.getMethod())) {
                permissionToUpdate.setName(permission.getName());
                permissionToUpdate.setModule(permission.getModule());
                permissionToUpdate.setApiPath(permission.getApiPath());
                permissionToUpdate.setMethod(permission.getMethod());
                return this.permissionRepository.save(permissionToUpdate);
            }
        }
        return null;
    }

    public void deletePermissionById(Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);
        Permission permissionToDelete = permission.get();
        permissionToDelete.getRoles().forEach(r -> r.getPermissions().remove(permissionToDelete));
        this.permissionRepository.delete(permissionToDelete);
    }
}
