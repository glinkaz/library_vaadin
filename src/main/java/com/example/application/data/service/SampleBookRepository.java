package com.example.application.data.service;

import com.example.application.data.entity.SampleBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleBookRepository extends JpaRepository<SampleBook, Integer> {

}