package com.example.loginandregistration.service;

import com.example.loginandregistration.model.Experience;
import com.example.loginandregistration.repository.EducationRepository;
import com.example.loginandregistration.repository.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ExperienceService implements Service<Experience> {

    private ExperienceRepository experienceRepository;

    @Autowired
    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @Override
    public Experience save(Experience experience) {
        return experienceRepository.save(experience);
    }

    @Override
    public void deleteById(Integer id) {
        experienceRepository.deleteById(id);
    }

    @Override
    public List<Experience> findAll() {
        return experienceRepository.findAll();
    }

    @Override
    public Experience getById(Integer id) {
        return experienceRepository.findById(id).get();
    }
}
