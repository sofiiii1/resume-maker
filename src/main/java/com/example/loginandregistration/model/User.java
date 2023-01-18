package com.example.loginandregistration.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min=2,max=30, message="First name should be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Name should contain only letters")
    @Column(name="first_name", nullable = false)
    private String firstName;
    @Size(min=2,max=30, message="Last name should be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Name should contain only letters")
    @Column(name="last_name", nullable = false)
    private String lastName;
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Resume> resumes;


}
