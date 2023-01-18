package com.example.loginandregistration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private String skillName;
    private String skillRating;
    private Integer resumeId;
}
