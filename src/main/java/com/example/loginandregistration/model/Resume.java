package com.example.loginandregistration.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="resume")
@NoArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(min=2,max=30, message="First name should be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Name should contain only letters")
    @Column(name="first_name")
    private String firstName;
    @Size(min=2,max=30, message="Last name should be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Name should contain only letters")
    @Column(name="last_name")
    private String lastName;
    @Past(message = "Date is invalid")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @Column(name="date_of_birth")
    private Date dateOfBirth;

    @Pattern(regexp = "^[0-9]*$", message = "Phone can contain only numbers")
    @Size(min = 9,max = 9, message = "Phone number should contain 9 numbers")
    private String phone;
    @Email(message="Email should be valid")
    private String email;

    @Column(length = 2000)
    private String biography;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<Education> educationList=new ArrayList<>();
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<Experience> experienceList=new ArrayList<>();
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<Skill> skillList=new ArrayList<>();
}
