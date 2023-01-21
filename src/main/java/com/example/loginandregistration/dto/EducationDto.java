package com.example.loginandregistration.dto;

import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationDto {
    private String institution;
    private String faculty;
    @Past(message = "Date is invalid")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date dateFrom;
    @Past(message = "Date is invalid")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date dateTo;
    private String description;
    private Integer resumeId;
}
