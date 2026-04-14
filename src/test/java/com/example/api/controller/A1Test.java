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
	public class A1Test {

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

	    @Test
	    void createBook_shouldReturnCreated() throws Exception {
	        when(bookService.createBook(eq(1L), any(BookDTO.class))).thenReturn(sampleBook);
	        mockMvc.perform(post("/authors/1/books")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(sampleBook)))
	                .andExpect(status().isCreated())
	                .andExpect(jsonPath("$.id").value(1));
	    }
	    
	    
	}
	
	
