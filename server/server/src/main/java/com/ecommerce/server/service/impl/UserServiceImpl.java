package com.ecommerce.server.service.impl;

import com.ecommerce.server.dto.UserDto;
import com.ecommerce.server.models.UserEntity;
import com.ecommerce.server.repository.UserRepository;
import com.ecommerce.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository userRepository;
    @Autowired

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




    @Override
    public List<UserDto> getAllUser() {
        System.out.println(" i am in get all user service");
        System.out.println( SecurityContextHolder.getContext().getAuthentication());
        List<UserEntity> users=  userRepository.findAll();
        return users.stream().map(u-> mapToDto(u)).collect(Collectors.toList());
    }


    private UserDto mapToDto(UserEntity user){
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedDate(user.getCreatedDate());
        userDto.setId(user.getId());

        return userDto;
    }
}
