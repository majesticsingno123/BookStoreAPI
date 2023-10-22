package com.bookstore.service;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    // add new book
    public void addNewBook(Book book) {
        com.bookstore.entity.Book bookEntity = new com.bookstore.entity.Book();
        bookEntity.setIsbn(book.getIsbn());
        bookEntity.setTitle(book.getTitle());
        bookEntity.setPrice(book.getPrice());
        bookEntity.setPublicationYear(book.getYear());
        bookEntity.setGenre(book.getGenre());

        List<com.bookstore.entity.Author> authors = convertAuthors(book.getAuthors());
        authorRepository.saveAll(authors);  // Save authors

        bookEntity.setAuthors(authors);
        bookRepository.save(bookEntity);
    }

    private List<com.bookstore.entity.Author> convertAuthors(List<com.bookstore.model.Author> authors) {
        System.out.println(authors.toString());
        return authors.stream()
                .map(this::convertAuthor)
                .collect(Collectors.toList());
    }

    // Convert Author to AuthorEntity
    private com.bookstore.entity.Author convertAuthor(com.bookstore.model.Author author) {
        com.bookstore.entity.Author authorEntity = new com.bookstore.entity.Author();
        authorEntity.setName(author.getName());
        authorEntity.setBirthday(author.getBirthday());
        return authorEntity;
    }

    // update book
    @Transactional
    public boolean updateBook(String isbn, Book updatedBook) {
        try {
            com.bookstore.entity.Book existingBookEntity = bookRepository.findBookEntityByIsbn(isbn);

            if (existingBookEntity != null) {
                // Update the fields of the existing book entity
                existingBookEntity.setTitle(updatedBook.getTitle());
                existingBookEntity.setPrice(updatedBook.getPrice());
                existingBookEntity.setPublicationYear(updatedBook.getYear());
                existingBookEntity.setGenre(updatedBook.getGenre());

                // Update associated authors (assuming the list of authors can be updated)
                List<com.bookstore.entity.Author> updatedAuthors = convertAuthors(updatedBook.getAuthors());
                existingBookEntity.setAuthors(updatedAuthors);

                authorRepository.saveAll(updatedAuthors);
                bookRepository.save(existingBookEntity);

                log.info("Book updated Successfully");
                return true;
            } else {
                log.info("Book not found with ISBN: " + isbn);
                return false; // Book was not found
            }
        } catch (Exception ex) {
            log.error("Error updating book", ex);
            return false;
        }
    }

    public List<Book> findBooksByTitle(String bookTitle) {
        try {
            List<com.bookstore.entity.Book> bookEntities = bookRepository.findBookEntityByTitle(bookTitle);

            return bookEntities.stream()
                    .map(this::convertBookToModel)  // Change this line
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding books by title", e);
            throw new RuntimeException("Error finding books by title", e);
        }
    }

    private Book convertBookToModel(com.bookstore.entity.Book bookEntity) {
        return Book.builder()
                .isbn(bookEntity.getIsbn())
                .title(bookEntity.getTitle())
                .authors(convertAuthorsToModels(bookEntity.getAuthors()))
                .year(bookEntity.getPublicationYear())  // Change this line
                .genre(bookEntity.getGenre())
                .price(bookEntity.getPrice())
                .build();
    }

    private List<Author> convertAuthorsToModels(List<com.bookstore.entity.Author> authors) {
        return authors.stream()
                .map(this::convertAuthorToModel)
                .collect(Collectors.toList());
    }

    private Author convertAuthorToModel(com.bookstore.entity.Author authorEntity) {
        return Author.builder()
                .name(authorEntity.getName())
                .birthday(Date.valueOf(authorEntity.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                .build();
    }

    //delete book
    @Transactional
    public boolean removeBook(String isbn) {
        try {
            com.bookstore.entity.Book bookEntity = bookRepository.findBookEntityByIsbn(isbn);

            if (bookEntity != null) {
                // Get associated authors
                List<com.bookstore.entity.Author> authors = bookEntity.getAuthors();

                // Delete the book
                bookRepository.delete(bookEntity);

                // Delete associated authors
                for (com.bookstore.entity.Author author : authors) {
                    authorRepository.delete(author);
                }

                log.info("Book and associated authors deleted Successfully");
                return true;
            } else {
                log.info("Book not found with ISBN: " + isbn);
                return false; // Book was not found
            }
        } catch (Exception ex) {
            log.error("Error deleting book and associated authors", ex);
            return false;
        }
    }
}
