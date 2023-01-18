package com.example.loginandregistration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String company;
    private String position;
    @Column(name="date_from")
    private LocalDate dateFrom;
    @Column(name="date_to")
    private LocalDate dateTo;
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    public Experience(String company, String position, LocalDate dateFrom, LocalDate dateTo, String description) {
        this.company = company;
        this.position = position;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.description = description;
    }
}
