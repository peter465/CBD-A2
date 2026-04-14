package com.example.api.controller;

import com.example.api.dto.BookDTO;
import com.example.api.exception.BadRequestException;
import com.example.api.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class GlobalBookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate pagination
        if (page < 0) {
            throw new BadRequestException("Page number cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Page size must be between 1 and 100");
        }

        // Parse dates
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        // Get paginated books
        Page<BookDTO> bookPage = bookService.getAllBooks(start, end, sortBy, order, page, size);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("books", bookPage.getContent());
        
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", bookPage.getNumber());
        pagination.put("pageSize", bookPage.getSize());
        pagination.put("totalElements", bookPage.getTotalElements());
        pagination.put("totalPages", bookPage.getTotalPages());
        pagination.put("isFirst", bookPage.isFirst());
        pagination.put("isLast", bookPage.isLast());
        pagination.put("hasNext", bookPage.hasNext());
        pagination.put("hasPrevious", bookPage.hasPrevious());
        
        response.put("pagination", pagination);

        return ResponseEntity.ok(response);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format. Expected YYYY-MM-DD");
        }
    }
}