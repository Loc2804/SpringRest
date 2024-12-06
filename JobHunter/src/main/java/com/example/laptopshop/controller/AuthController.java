package com.example.laptopshop.controller;

import com.example.laptopshop.domain.User;
import com.example.laptopshop.domain.response.user.ResCreateUserDTO;
import com.example.laptopshop.service.UserService;
import com.example.laptopshop.util.annotation.ApiMessage;
import com.example.laptopshop.util.error.InvalidIdException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.example.laptopshop.domain.request.ReqLoginDTO;
import com.example.laptopshop.domain.response.ResLoginDTO;
import com.example.laptopshop.util.SecurityUtil;

import jakarta.validation.Valid;

import javax.sound.midi.InvalidMidiDataException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpriation; // ở file env -> string -> khi được gọi tự động ép thành kiểu long khi gọi ra
    private final PasswordEncoder passwordEncoder;
    // dependencies injection
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // nạp username và password vào security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        // xác thực người dùng -> viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create access token

        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User currentUser = this.userService.handleGetUserByUsername(username);
        if (currentUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(),currentUser.getEmail(),currentUser.getName(), currentUser.getRole());
            res.setUserLogin(userLogin);
        }
        String token = this.securityUtil.createAccessToken(authentication.getName(),res);
        res.setToken(token);

        //create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(username,res);
        //update user
        this.userService.updateUserToken(refreshToken, username);

        //set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpriation)
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User currentUser = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getName());
            userGetAccount.setUser(userLogin);
            userLogin.setRole(currentUser.getRole());
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken( @CookieValue(name = "refreshToken", defaultValue = "abc") String refreshToken) throws InvalidMidiDataException {
        if(refreshToken.equals("abc")){
            throw new InvalidMidiDataException("Refresh token is error");
        }
        Jwt jwt = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = jwt.getSubject();
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if(currentUser == null) {
            throw new InvalidMidiDataException("Refresh token is incorrect");
        }
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(),currentUserDB.getEmail(),currentUserDB.getName(), currentUserDB.getRole());
            res.setUserLogin(userLogin);
        }
        String token = this.securityUtil.createAccessToken(email,res);
        res.setToken(token);

        //create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email,res);
        //update user
        this.userService.updateUserToken(newRefreshToken, email);

        //set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpriation)
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("User logout")
    public ResponseEntity<Void> Logout() throws InvalidIdException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if(email.equals(""))
        {
            throw new InvalidIdException("Token is incorrect");
        }

        this.userService.updateUserToken(null, email);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,deleteCookie.toString()).body(null);
    }
    @PostMapping("/auth/register")
    @ApiMessage("Register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postUser) throws InvalidIdException{
        boolean isEmailExist = this.userService.isEmailExist(postUser.getEmail());
        if(isEmailExist){
            throw new InvalidIdException("Email " + postUser.getEmail() + " already exists");
        }
        String hashPassword = this.passwordEncoder.encode(postUser.getPassword());
        postUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResCreateUserDTO(postUser));
    }
}
