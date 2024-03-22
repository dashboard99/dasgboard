package com.ecommerce.server.controller;


import com.ecommerce.server.dto.AuthResponseDTO;
import com.ecommerce.server.dto.LoginDto;
import com.ecommerce.server.dto.RegisterDto;
import com.ecommerce.server.models.Role;
import com.ecommerce.server.models.UserEntity;
import com.ecommerce.server.repository.RoleRepository;
import com.ecommerce.server.repository.UserRepository;
import com.ecommerce.server.security.Jwtgenerator;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;

    private Jwtgenerator jwtgenerator;
    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository,AuthenticationManager authenticationManager,Jwtgenerator jwtgenerator) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtgenerator = jwtgenerator;
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login (@RequestBody LoginDto loginDto){

        Authentication authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authResult);
        String token = jwtgenerator.generateToken(authResult);
        return new ResponseEntity<>(new AuthResponseDTO(token),HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> register (@RequestBody RegisterDto registerDto){
       if(userRepository.existsByUsername(registerDto.getUsername())){
           return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
       }

       UserEntity user = new UserEntity();
       user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success",HttpStatus.OK);


    }
}
