package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

@Entity(name = "books")
public class Book extends AbstractEntity {

    @Lob
    private String image;
    private String name;
    private String author;
    private LocalDate publicationDate;
    private Integer pages;
    private String isbn;

    @ManyToMany(
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @JoinTable(
            name = "books_tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public void addTag(Tag tag){
        this.tags.add(tag);
        tag.books.add(this);
    }

    public void remove(Tag tag){
        this.tags.remove(tag);
        tag.books.remove(this);
    }
    //czy pożyczona i komu
    //gdzie sie znajduje
    //uzytkownik ma biblioteke jedna
    //adm wyświetla liste uzytownikow
    //user dodaje, usuwa i edytuje ksiązki, wyszukuje po tagach itp
    //czy czytami na której stronie jestesmy

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

}
