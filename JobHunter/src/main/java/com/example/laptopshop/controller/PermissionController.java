package com.example.laptopshop.controller;

import com.example.laptopshop.domain.Permission;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.repository.PermissionRepository;
import com.example.laptopshop.service.PermissionService;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.example.laptopshop.util.error.InvalidIdException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;
    private final PermissionRepository permissionRepository;
    public PermissionController(PermissionService permissionService, PermissionRepository permissionRepository) {
        this.permissionService = permissionService;
        this.permissionRepository = permissionRepository;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create new permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws InvalidIdException {
        if(this.permissionService.checkExistPermission(permission)) {
            throw new InvalidIdException("Permission already exist");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.savePermission(permission));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get a permission")
    public ResponseEntity<Permission> getPermission(@PathVariable("id") long id) throws InvalidIdException {
        Permission permission = this.permissionService.findPermissionById(id);
        if(permission == null) {
            throw new InvalidIdException("Permission does not exist");
        }
        return ResponseEntity.ok().body(this.permissionService.findPermissionById(id));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec, Pageable pageable){
        return ResponseEntity.ok().body(this.permissionService.getAllPermissions(spec, pageable));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws InvalidIdException {
        if(this.permissionService.checkExistPermission(permission)) {
            if(this.permissionService.isSameName(permission)){
                throw new InvalidIdException("Permission name already exists");
            }
        }
        Permission updatedPermission = this.permissionService.updatePermission(permission);
        if(updatedPermission == null) {
            throw new InvalidIdException("Permission does not exist");
        }
        return ResponseEntity.ok().body(updatedPermission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws InvalidIdException {
        Permission permission = this.permissionService.findPermissionById(id);
        if(permission == null) {
            throw new InvalidIdException("Permission does not exist");
        }
        this.permissionService.deletePermissionById(id);
        return ResponseEntity.ok().body(null);
    }
}
