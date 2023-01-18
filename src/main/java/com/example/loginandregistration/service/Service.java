package com.example.loginandregistration.service;

import java.util.List;

public interface Service <T>{
    T save(T t);
    void deleteById(Integer id);
    List<T> findAll();
    T getById(Integer id);

}
