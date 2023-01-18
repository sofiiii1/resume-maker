package com.example.loginandregistration.service;

import com.example.loginandregistration.model.User;
import com.example.loginandregistration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        User secureUser=new User();
        secureUser.setFirstName(user.getFirstName());
        secureUser.setLastName(user.getLastName());
        secureUser.setEmail(user.getEmail());
        secureUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(secureUser);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findALlUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).get();
    }
}
