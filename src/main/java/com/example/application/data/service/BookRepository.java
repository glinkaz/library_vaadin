package com.example.application.data.service;

import com.example.application.data.entity.Book;
import com.example.application.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    Page<Book> findAllByOwner(User owner, Pageable pageable);
    List<Book> findAllByOwner(User owner);
}