package com.example.loginandregistration.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="education")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String institution;
    private String faculty;
    @Column(name = "date_from")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFrom;
    @Column(name = "date_to")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateTo;
    @Column(length = 2000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    public Education(String institution, String faculty, Date dateFrom, Date dateTo, String description) {
        this.institution = institution;
        this.faculty = faculty;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.description = description;
    }
}