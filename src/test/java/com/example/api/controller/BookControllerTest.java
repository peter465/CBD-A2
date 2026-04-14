package com.example.api.controller;

import com.example.api.dto.BookDTO;
import com.example.api.exception.BadRequestException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private BookDTO sampleBook;
    private Page<BookDTO> samplePage;

    @BeforeEach
    void setUp() {
        // Build MockMvc instance (no Spring context)
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

        // Configure ObjectMapper to handle Java 8 dates
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Prepare test data
        sampleBook = new BookDTO();
        sampleBook.setId(1L);
        sampleBook.setTitle("Sample Book");
        sampleBook.setPublicationDate(LocalDate.of(2023, 1, 1));
        sampleBook.setPrice(19.99);
        sampleBook.setAuthorId(1L);
        sampleBook.setAuthorName("John Doe");

        samplePage = new PageImpl<>(List.of(sampleBook), PageRequest.of(0, 10), 1);
    }

    // ==================== CREATE BOOK ====================
    @Test
    void createBook_shouldReturnCreated() throws Exception {
        when(bookService.createBook(eq(1L), any(BookDTO.class))).thenReturn(sampleBook);

        mockMvc.perform(post("/authors/1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sample Book"));
    }

    // ==================== GET BOOKS (paginated) ====================
    @Test
    void getBooks_shouldReturnPaginatedResponse() throws Exception {
        when(bookService.getBooksByAuthorPaginated(eq(1L), isNull(), isNull(), isNull(), eq("asc"), eq(0), eq(10)))
                .thenReturn(samplePage);

        mockMvc.perform(get("/authors/1/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.totalElements").value(1));
    }

    

    

    

    @Test
    void getBooks_withValidDateRange_shouldParseAndPassToService() throws Exception {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        when(bookService.getBooksByAuthorPaginated(eq(1L), eq(start), eq(end), isNull(), eq("asc"), eq(0), eq(10)))
                .thenReturn(samplePage);

        mockMvc.perform(get("/authors/1/books")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isOk());

        verify(bookService).getBooksByAuthorPaginated(eq(1L), eq(start), eq(end), isNull(), eq("asc"), eq(0), eq(10));
    }

    

    
   

    // ==================== UPDATE BOOK ====================
    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        BookDTO updated = new BookDTO();
        updated.setId(1L);
        updated.setTitle("Updated Title");
        updated.setPublicationDate(LocalDate.now());
        updated.setPrice(29.99);
        updated.setAuthorId(1L);
        when(bookService.updateBook(eq(1L), eq(1L), any(BookDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/authors/1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    // ==================== DELETE BOOK ====================
    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L, 1L);

        mockMvc.perform(delete("/authors/1/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L, 1L);
    }
}


