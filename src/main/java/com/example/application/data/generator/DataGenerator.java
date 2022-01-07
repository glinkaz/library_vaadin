package com.example.application.data.generator;

import com.example.application.data.Role;
import com.example.application.data.entity.Book;
import com.example.application.data.entity.Tag;
import com.example.application.data.entity.User;
import com.example.application.data.service.BookRepository;
import com.example.application.data.service.TagRepository;
import com.example.application.data.service.UserRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, BookRepository bookRepository,
                                      UserRepository userRepository, TagRepository tagRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (bookRepository.count() != 0L && tagRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Sample Book entities...");
            ExampleDataGenerator<Book> sampleBookRepositoryGenerator = new ExampleDataGenerator<>(
                    Book.class, LocalDateTime.of(2022, 1, 5, 0, 0, 0));
            sampleBookRepositoryGenerator.setData(Book::setId, DataType.ID);
            sampleBookRepositoryGenerator.setData(Book::setImage, DataType.BOOK_IMAGE_URL);
            sampleBookRepositoryGenerator.setData(Book::setName, DataType.BOOK_TITLE);
            sampleBookRepositoryGenerator.setData(Book::setAuthor, DataType.FULL_NAME);
            sampleBookRepositoryGenerator.setData(Book::setPublicationDate, DataType.DATE_OF_BIRTH);
            sampleBookRepositoryGenerator.setData(Book::setPages, DataType.NUMBER_UP_TO_1000);
            sampleBookRepositoryGenerator.setData(Book::setIsbn, DataType.EAN13);
            bookRepository.saveAll(sampleBookRepositoryGenerator.create(10, seed));

            logger.info("... generating 10 Sample Tag entities...");
            ExampleDataGenerator<Tag> sampleTagRepositoryGenerator = new ExampleDataGenerator<>(
                    Tag.class, LocalDateTime.of(2022, 1, 5, 0, 0, 0));
            sampleTagRepositoryGenerator.setData(Tag::setId, DataType.ID);
            sampleTagRepositoryGenerator.setData(Tag::setName, DataType.OCCUPATION);
            tagRepository.saveAll(sampleTagRepositoryGenerator.create(3, seed));

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()));
            userRepository.save(admin);

            logger.info("Generated demo data");
        };
    }

}