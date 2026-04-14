package com.example.api.service.impl;

import com.example.api.dto.BookDTO;
import com.example.api.entity.Author;
import com.example.api.entity.Book;
import com.example.api.exception.BadRequestException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.AuthorRepository;
import com.example.api.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Book book;
    private BookDTO dto;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("John Doe");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPublicationDate(LocalDate.of(2023,1,1));
        book.setPrice(19.99);
        book.setAuthor(author);

        dto = new BookDTO();
        dto.setTitle("Test Book");
        dto.setPublicationDate(LocalDate.of(2023,1,1));
        dto.setPrice(19.99);
    }

    @Test
    void createBook_shouldSaveAndReturnBook() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDTO result = bookService.createBook(1L, dto);

        assertThat(result.getTitle()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_authorNotFound_shouldThrow() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createBook(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found");
    }

    @Test
    void createBook_negativePrice_shouldThrow() {
        dto.setPrice(-10.0);
        assertThatThrownBy(() -> bookService.createBook(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("price cannot be negative");
    }
}