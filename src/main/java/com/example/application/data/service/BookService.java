package com.example.application.data.service;

import com.example.application.data.Role;
import com.example.application.data.entity.Book;
import java.util.List;
import java.util.Optional;

import com.example.application.data.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookService {

    private BookRepository bookRepository;

    public BookService(@Autowired BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> get(Integer id) {
        return bookRepository.findById(id);
    }

    public Book update(Book entity) {
        log.info("Update book - " + entity.getName());
        return bookRepository.save(entity);
    }

    public Book save(Book entity) {
        //if exists - dont update book
//        if (bookRepository.exists(entity)){
//            throw new IllegalArgumentException("Book exists in library");
//        }
//        entity.setOwner(user);
        return bookRepository.save(entity);
    }

    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }

    public int count() {
        return (int) bookRepository.count();
    }

    public List<Book> getBooks(User user) {
        if (user.getRoles().contains(Role.ADMIN)){
            return bookRepository.findAll();
        }
        return bookRepository.findAllByOwner(user);
    }
}
