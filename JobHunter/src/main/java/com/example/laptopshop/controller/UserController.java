package com.example.laptopshop.controller;

import com.example.laptopshop.domain.response.user.ResCreateUserDTO;
import com.example.laptopshop.domain.response.user.ResUpdateUserDTO;
import com.example.laptopshop.domain.response.user.ResUserDTO;
import com.example.laptopshop.domain.response.ResultPaginationDTO;
import com.example.laptopshop.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.laptopshop.domain.User;
import com.example.laptopshop.service.UserService;
import com.example.laptopshop.util.error.InvalidIdException;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postUser) throws InvalidIdException{
        boolean isEmailExist = this.userService.isEmailExist(postUser.getEmail());
        if(isEmailExist){
            throw new InvalidIdException("Email " + postUser.getEmail() + " already exists");
        }
        String hashPassword = this.passwordEncoder.encode(postUser.getPassword());
        postUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResCreateUserDTO(postUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws InvalidIdException {
        User user = this.userService.handleGetUserById(id);
        if(user == null){
            throw new InvalidIdException("User not found");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete user");
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get a user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws InvalidIdException{
        User user = this.userService.handleGetUserById(id);
        if(user == null){
            throw new InvalidIdException("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleGetAllUser(spec,pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> putMethodName(@RequestBody User putUser) throws InvalidIdException {
        User user = this.userService.handleUpdateUser(putUser);
        if(user == null){
            throw new InvalidIdException("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.covertToResUpdateUserDTO(user));
    }


}
