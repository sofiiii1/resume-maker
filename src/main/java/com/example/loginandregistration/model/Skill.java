package com.example.loginandregistration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="skill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "skill_name")
    private String skillName;
    @Column(name="skill_rating")
    private Enum skillRating;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    public Skill(String skillName, Enum skillRating) {
        this.skillName = skillName;
        this.skillRating = skillRating;
    }
}
