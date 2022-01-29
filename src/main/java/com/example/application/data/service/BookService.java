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

    public Book save(User user, Book entity) {
        //if exists - dont update book
//        if (bookRepository.exists(entity)){
//            throw new IllegalArgumentException("Book exists in library");
//        }
        entity.setOwner(user);
        return bookRepository.save(entity);
    }

    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }

    public Page<Book> list(User user, Pageable pageable) {
        return bookRepository.findAllByOwner(user, pageable);
    }

    public int count() {
        return (int) bookRepository.count();
    }




//    /**
//     * The contents of this method are just for demo purposes and irrelevant for the features that want to be presented.
//     * @param offset
//     * @param limit
//     * @param nameFilter
//     * @param lastNameFilter
//     * @param sortOrders
//     * @return
//     */
//    public Stream<Book> fetchBooks(int offset, int limit, String nameFilter, String lastNameFilter, List<BookSort> sortOrders) {
//        System.out.println("Offset: " + offset + ", limit: " + limit + ", nameFilter='" + nameFilter + "', lastNameFilter = '" + lastNameFilter + "', sortOrders = " + sortOrders);
//        Comparator<Book> comparator = (o1, o2)->0;
//        for (BookSort bookSort : sortOrders) {
//            switch (bookSort.getPropertyName()) {
//                case BookSort.NAME:
//                    comparator = comparator.thenComparing(Book::getName);
//                    break;
//                case BookSort.AUTHOR:
//                    comparator = comparator.thenComparing(Book::getAuthor);
//                    break;
//            }
//            if (!bookSort.isDescending()) comparator = comparator.reversed();
//        }
//        List<Book> sortedList = new LinkedList<>(bookRepository.findAll());
//        sortedList.sort(comparator);
//        List<Book> result = sortedList.stream()
//                .filter(person->person.getName().contains(nameFilter==null?"":nameFilter))
//                .filter(person->person.getAuthor().contains(lastNameFilter==null?"":lastNameFilter))
//                .skip(offset)
//                .limit(limit).collect(Collectors.toList());
//        System.out.println("Size: " + result.size());
//        return result.stream();
//    }
//    public int getPersonCount(String nameFilter, String authorFilter) {
//        List<Book> result = bookRepository.findAll().stream()
//                .filter(person->person.getName().contains(nameFilter==null?"":nameFilter))
//                .filter(person->person.getAuthor().contains(authorFilter==null?"":authorFilter))
//                .collect(Collectors.toList());
//        return result.size();
//    }

    public List<Book> getBooks(User user) {
        if (user.getRoles().equals(Role.ADMIN)){
            return bookRepository.findAll();
        }
        return bookRepository.findAllByOwner(user);
    }
}
