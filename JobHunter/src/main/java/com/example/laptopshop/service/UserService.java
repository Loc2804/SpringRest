package com.example.laptopshop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.laptopshop.domain.response.ResCreateUserDTO;
import com.example.laptopshop.domain.response.ResUpdateUserDTO;
import com.example.laptopshop.domain.response.ResUserDTO;
import com.example.laptopshop.domain.response.ResultPaginationDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.laptopshop.domain.User;
import com.example.laptopshop.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.handleGetUserById(user.getId());
        if (currentUser != null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            currentUser.setName(user.getName());
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResultPaginationDTO handleGetAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> users = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setTotal(users.getTotalElements());
        meta.setPages(users.getTotalPages());

        rs.setMeta(meta);
        List<ResUserDTO> userList = users.getContent().stream()
                .map(item -> new ResUserDTO(item.getId(), item.getName(), item.getEmail(),
                        item.getAge(), item.getAddress(), item.getGender(),
                        item.getCreatedAt(), item.getUpdatedAt(),
                        item.getCreatedBy(), item.getUpdatedBy()))
                .collect(Collectors.toList());
        rs.setResult(userList);
        return rs;
    }

    public ResCreateUserDTO convertResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setCreatedBy(user.getCreatedBy());
        return res;
    }

    public ResUserDTO convertResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setCreatedBy(user.getCreatedBy());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setUpdatedBy(user.getUpdatedBy());
        return res;
    }

    public ResUpdateUserDTO covertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setUpdatedBy(user.getUpdatedBy());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
