package com.ecommerce.server.service;

import com.ecommerce.server.dto.UserDto;
import com.ecommerce.server.models.UserEntity;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUser();
}
