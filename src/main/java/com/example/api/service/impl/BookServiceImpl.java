package com.example.api.service.impl;

import com.example.api.dto.BookDTO;
import com.example.api.entity.Author;
import com.example.api.entity.Book;
import com.example.api.exception.BadRequestException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.AuthorRepository;
import com.example.api.repository.BookRepository;
import com.example.api.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;  // ← Add this import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;  // ← Add this import
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;
    
    private BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setPrice(book.getPrice());
        
        // Add author info
        if (book.getAuthor() != null) {
            dto.setAuthorId(book.getAuthor().getId());
            dto.setAuthorName(book.getAuthor().getName());
        }
        
        return dto;
    }

    private Book fromDTO(BookDTO dto) {
        Book b = new Book();
        b.setTitle(dto.getTitle());
        b.setPublicationDate(dto.getPublicationDate());
        b.setPrice(dto.getPrice());
        return b;
    }

    @Override
    @Transactional
    public BookDTO createBook(Long authorId, BookDTO dto) {
        if (dto.getPublicationDate() == null) {
            throw new BadRequestException("publicationDate is required (format: YYYY-MM-DD)");
        }
        if (dto.getPrice() != null && dto.getPrice() < 0) {
            throw new BadRequestException("price cannot be negative");
        }

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + authorId));

        Book book = fromDTO(dto);
        book.setAuthor(author);
        Book saved = bookRepository.save(book);
        return toDTO(saved);
    }

    @Override
    public BookDTO getBook(Long authorId, Long bookId) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id " + authorId);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        if (book.getAuthor() == null || !book.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Book with id " + bookId + " does not belong to author with id " + authorId);
        }

        return toDTO(book);
    }

    @Override
    public List<BookDTO> getBooksByAuthor(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id " + authorId);
        }
        return bookRepository.findByAuthorId(authorId).stream().map(this::toDTO).toList();
    }

    @Override
    public Page<BookDTO> getBooksByAuthorPaginated(Long authorId, LocalDate start, LocalDate end, 
                                                   String sortBy, String order, int page, int size) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id " + authorId);
        }

        Pageable pageable = createPageable(page, size, sortBy, order);
        
        // Create specification for author and date filters
        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by author
            predicates.add(cb.equal(root.get("author").get("id"), authorId));
            
            // Filter by date range
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publicationDate"), end));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        return bookPage.map(this::toDTO);
    }

    @Override
    public Page<BookDTO> getAllBooks(LocalDate start, LocalDate end, String sortBy, 
                                     String order, int page, int size) {
        
        Pageable pageable = createPageable(page, size, sortBy, order);
        
        // Create specification for date filters
        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publicationDate"), end));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        return bookPage.map(this::toDTO);
    }

    @Override
    public long getTotalBooksCount() {
        return bookRepository.count();
    }

    private Pageable createPageable(int page, int size, String sortBy, String order) {
        if (sortBy == null || sortBy.isEmpty()) {
            return PageRequest.of(page, size);
        }
        
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? 
                                   Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        
        return PageRequest.of(page, size, sort);
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long authorId, Long bookId, BookDTO dto) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id " + authorId);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        if (book.getAuthor() == null || !book.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Book with id " + bookId + " does not belong to author with id " + authorId);
        }

        if (dto.getPrice() != null && dto.getPrice() < 0) {
            throw new BadRequestException("price cannot be negative");
        }

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getPublicationDate() != null) {
            book.setPublicationDate(dto.getPublicationDate());
        }
        if (dto.getPrice() != null) {
            book.setPrice(dto.getPrice());
        }

        Book updated = bookRepository.save(book);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteBook(Long authorId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        if (book.getAuthor() == null || !book.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Book does not belong to the given author");
        }

        bookRepository.delete(book);
    }
}