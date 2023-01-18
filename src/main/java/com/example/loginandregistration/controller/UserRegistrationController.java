package com.example.loginandregistration.controller;

import com.example.loginandregistration.model.User;
import com.example.loginandregistration.repository.ResumeRepository;
import com.example.loginandregistration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserRegistrationController {
    private UserService userService;
    private ResumeRepository resumeRepository;

    @Autowired
    public UserRegistrationController(UserService userService,
                                      ResumeRepository resumeRepository) {
        this.userService = userService;
        this.resumeRepository = resumeRepository;
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model, Principal principal){
        List<User> users = userService.findALlUsers();
        model.addAttribute("content", "Hello "+principal.getName());
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/register")
    public String registerUserAccount(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register/save")
    public String showRegistrationForm(@ModelAttribute("user") @Valid User user, BindingResult result){
        User existingUser=userService.findByEmail(user.getEmail());
        if(existingUser!=null)
            result.reject("email", null, "There is already an account registered with that email");
        if (result.hasErrors()) {
            return "registration";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }


}
