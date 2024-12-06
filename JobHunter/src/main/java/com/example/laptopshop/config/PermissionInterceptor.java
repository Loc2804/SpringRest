package com.example.laptopshop.config;

import com.example.laptopshop.domain.Permission;
import com.example.laptopshop.domain.Role;
import com.example.laptopshop.domain.User;
import com.example.laptopshop.service.UserService;
import com.example.laptopshop.util.SecurityUtil;
import com.example.laptopshop.util.error.InvalidIdException;
import com.example.laptopshop.util.error.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;


public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println("Path from HandlerMapping: " + path);
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        if(email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if(user != null) {
                Role role = user.getRole();
                if(role != null) {
                    List<Permission> permissionList = role.getPermissions();
                    boolean isAllowed = permissionList.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if(isAllowed == false){
                        throw new PermissionException("You are not allowed to access this endpoint!");
                    }
                }
                else throw new PermissionException("You are not allowed to access this endpoint!");
            }
        }
        return true;
    }
}
