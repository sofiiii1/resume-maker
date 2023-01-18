package com.example.loginandregistration.service;

import com.example.loginandregistration.model.Skill;
import com.example.loginandregistration.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class SkillService implements Service<Skill>{

    private SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public void deleteById(Integer id) {
        skillRepository.deleteById(id);
    }

    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    @Override
    public Skill getById(Integer id) {
        return skillRepository.findById(id).get();
    }
}
