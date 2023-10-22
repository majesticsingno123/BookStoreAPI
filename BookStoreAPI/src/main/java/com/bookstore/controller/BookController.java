package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {

    @Autowired
    private BookService bookService;

    //AddBookController
    @Operation(summary = "Add a new book", tags = "Add", description = "{\"Book}")
    @PostMapping(value = "/add")
    @Transactional
    public ResponseEntity<String> addNewBookApi(@RequestBody final Book book) {
        bookService.addNewBook(book);
        return new ResponseEntity<>("Added a new book successfully", HttpStatus.OK);
    }

    //UpdateBook controller
    @Operation(summary = "Update book", tags = "Update", description = "{\"Book}")
    @PutMapping(value = "/update/{isbn}")
    @Transactional
    public ResponseEntity<String> updateBookApi(
            @PathVariable @NotBlank final @Valid String isbn,
            @RequestBody final Book updatedBook) {
        boolean result = bookService.updateBook(isbn, updatedBook);

        if (result) {
            return new ResponseEntity<>("Updated the book with ISBN: " + isbn, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found with ISBN: " + isbn, HttpStatus.NOT_FOUND);
        }
    }


    //find book by title and author controller
    @Operation(summary = "find books", tags = "Find", description = "")
    @GetMapping(value = "/find")
    @Transactional
    public ResponseEntity<String> findBookApi(@RequestParam  @NotBlank final @Valid String bookTitle) {
        List<Book> booksTitle = new ArrayList<>();
        try {
            booksTitle = bookService.findBooksByTitle(bookTitle);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to find book", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("The following Book is/are found : " + booksTitle, HttpStatus.OK);
    }

    // delete book controller
    @Operation(summary = "Delete book", tags = "Delete", description = "ISBN")
    @DeleteMapping(value = "/{isbn}")
    @Transactional
    public ResponseEntity<String> deleteBookApi(@PathVariable @NotBlank final @Valid String isbn) {
        boolean deleted = bookService.removeBook(isbn);

        if (deleted) {
            return new ResponseEntity<>("Deleted the following book with ISBN: " + isbn, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found with ISBN: " + isbn, HttpStatus.NOT_FOUND);
        }
    }
}
