package com.example.loginandregistration.service;

import com.example.loginandregistration.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User findByEmail(String email);
    List<User> findALlUsers();

    User getById(Long id);

}
