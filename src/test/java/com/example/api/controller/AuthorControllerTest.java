package com.example.api.controller;

import com.example.api.dto.AuthorDTO;
import com.example.api.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    private AuthorDTO authorDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setName("Test Author");
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllAuthors_ShouldReturnPageOfAuthors() {
        List<AuthorDTO> authors = Arrays.asList(authorDTO);
        Page<AuthorDTO> authorPage = new PageImpl<>(authors);
        when(authorService.getAllAuthors(pageable)).thenReturn(authorPage);

        ResponseEntity<Page<AuthorDTO>> response = authorController.getAllAuthors(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        verify(authorService).getAllAuthors(pageable);
    }

    @Test
    void getAuthorById_ShouldReturnAuthor() {
        when(authorService.getAuthorById(1L)).thenReturn(authorDTO);

        ResponseEntity<AuthorDTO> response = authorController.getAuthorById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Author", response.getBody().getName());
        verify(authorService).getAuthorById(1L);
    }

    @Test
    void createAuthor_ShouldReturnCreatedAuthor() {
        when(authorService.createAuthor(authorDTO)).thenReturn(authorDTO);

        ResponseEntity<AuthorDTO> response = authorController.createAuthor(authorDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(authorService).createAuthor(authorDTO);
    }
}