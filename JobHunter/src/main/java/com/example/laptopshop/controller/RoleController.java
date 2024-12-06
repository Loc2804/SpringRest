package com.example.laptopshop.controller;


import com.example.laptopshop.domain.Role;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.service.RoleService;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.example.laptopshop.util.error.InvalidIdException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody Role role) throws InvalidIdException {
        if(this.roleService.checkExistByName(role.getName())) {
            throw new InvalidIdException("Role name already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get a role")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws InvalidIdException {
        Role role = this.roleService.findRoleById(id);
        if (role == null) {
            throw new InvalidIdException("Role not found");
        }
        return ResponseEntity.ok().body(role);
    }

    @GetMapping("/roles")
    @ApiMessage("Get roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAllRoles(spec, pageable));
    }

    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws InvalidIdException {
        if(!this.roleService.checkExistById(role.getId())) {
            throw new InvalidIdException("Role not found");
        }
//        if(this.roleService.checkExistByName(role.getName())) {
//            throw new InvalidIdException("Role name already exists");
//        }
        Role updatedRole = this.roleService.updateRole(role);
        if (updatedRole == null) {
            throw new InvalidIdException("Role not found");
        }
        return ResponseEntity.ok().body(updatedRole);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws InvalidIdException {
        Role role = this.roleService.findRoleById(id);
        if (role == null) {
            throw new InvalidIdException("Role not found");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.ok().body(null);
    }
}
