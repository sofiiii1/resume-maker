package com.example.loginandregistration.service;

import com.example.loginandregistration.model.Education;
import com.example.loginandregistration.repository.EducationRepository;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class EducationService implements Service<Education> {
    private EducationRepository educationRepository;

    @Autowired
    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    @Override
    public Education save(Education education) {
        return educationRepository.save(education);
    }

    @Override
    public void deleteById(Integer id) {
        educationRepository.deleteById(id);
    }

    @Override
    public List<Education> findAll() {
        return educationRepository.findAll();
    }

    @Override
    public Education getById(Integer id) {
        return educationRepository.findById(id).get();
    }
}
