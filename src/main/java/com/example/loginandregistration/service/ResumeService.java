package com.example.loginandregistration.service;

import com.example.loginandregistration.model.Resume;
import com.example.loginandregistration.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@org.springframework.stereotype.Service
public class ResumeService implements Service<Resume>{

    private ResumeRepository resumeRepository;

    @Autowired
    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    public Resume save(Resume resume) {
        return resumeRepository.save(resume);
    }

    @Override
    public void deleteById(Integer id) {
        resumeRepository.deleteById(id);
    }

    @Override
    public List<Resume> findAll() {
        return resumeRepository.findAll();
    }

    @Override
    public Resume getById(Integer id) {
        return resumeRepository.findById(id).get();
    }


}
