package com.example.loginandregistration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDto {
    private String company;
    private String position;
    private Date dateFrom;
    private Date dateTo;
    private String description;
    private Integer resumeId;
}
