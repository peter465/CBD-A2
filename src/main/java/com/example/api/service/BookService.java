package com.example.api.service;

import com.example.api.dto.BookDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface BookService {
    // Create
    BookDTO createBook(Long authorId, BookDTO dto);
    
    // Read - Author specific
    BookDTO getBook(Long authorId, Long bookId);
    List<BookDTO> getBooksByAuthor(Long authorId);
    Page<BookDTO> getBooksByAuthorPaginated(Long authorId, LocalDate start, LocalDate end, 
                                           String sortBy, String order, int page, int size);
    
    // Read - Global
    Page<BookDTO> getAllBooks(LocalDate start, LocalDate end, String sortBy, 
                             String order, int page, int size);
    long getTotalBooksCount();
    
    // Update
    BookDTO updateBook(Long authorId, Long bookId, BookDTO dto);
    
    // Delete
    void deleteBook(Long authorId, Long bookId);

	List<BookDTO> getBooksFilteredAndSorted(Long authorId, LocalDate start, LocalDate end, String sortBy, String order);
}