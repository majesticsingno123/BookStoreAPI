package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Book {
    String isbn;
    String title;
    List<Author> authors;
    int year;
    String genre;
    double price;
}


