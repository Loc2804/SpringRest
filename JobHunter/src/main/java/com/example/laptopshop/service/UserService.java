package com.example.laptopshop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.laptopshop.domain.Company;
import com.example.laptopshop.domain.response.ResCreateUserDTO;
import com.example.laptopshop.domain.response.ResUpdateUserDTO;
import com.example.laptopshop.domain.response.ResUserDTO;
import com.example.laptopshop.domain.response.ResultPaginationDTO;

import com.example.laptopshop.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.laptopshop.domain.User;
import com.example.laptopshop.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository,CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User handleCreateUser(User user) {
        if(user.getCompany() !=null){
            Optional<Company> company = this.companyService.findById(user.getCompany().getId());
            user.setCompany(company.isPresent()?company.get():null);
        }
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.handleGetUserById(user.getId());
        if (currentUser != null) {
            if(user.getCompany() !=null){
                Optional<Company> company = this.companyService.findById(user.getCompany().getId());
                user.setCompany(company.isPresent()?company.get():null);
                currentUser.setAddress(user.getAddress());
                currentUser.setAge(user.getAge());
                currentUser.setGender(user.getGender());
                currentUser.setName(user.getName());
                currentUser.setCompany(user.getCompany());
                currentUser = this.userRepository.save(currentUser);
            }
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
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : ""
                        ),
                        item.getCreatedAt(), item.getUpdatedAt(),
                        item.getCreatedBy(), item.getUpdatedBy()))
                .collect(Collectors.toList());
        rs.setResult(userList);
        return rs;
    }

    public ResCreateUserDTO convertResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if(user.getCompany()!=null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
        res.setCreatedAt(user.getCreatedAt());
        res.setCreatedBy(user.getCreatedBy());
        return res;
    }

    public ResUserDTO convertResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        if(user.getCompany()!=null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
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
        ResUpdateUserDTO.CompanyUser companyUser = new  ResUpdateUserDTO.CompanyUser();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        if(user.getCompany()!=null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
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
