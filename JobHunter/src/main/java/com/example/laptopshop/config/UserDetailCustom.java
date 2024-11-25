package com.example.laptopshop.config;

import java.util.Collections;

import com.example.laptopshop.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; //trùng tên với User do mình tạo 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {

    private final UserService userService;

    public UserDetailCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.laptopshop.domain.User user = this.userService.handleGetUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/Password is incorrect!");
        }
        return new User(
                user.getEmail(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
