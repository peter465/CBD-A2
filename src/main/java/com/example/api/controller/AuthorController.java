package com.example.api.controller;

import com.example.api.dto.AuthorDTO;
import com.example.api.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldReturnCreatedAuthor() {
        AuthorDTO input = new AuthorDTO(null, "John Doe");
        AuthorDTO saved = new AuthorDTO(1L, "John Doe");

        when(authorService.createAuthor(input)).thenReturn(saved);

        ResponseEntity<AuthorDTO> response = authorController.create(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
        verify(authorService).createAuthor(input);
    }

    @Test
    void get_ShouldReturnAuthor() {
        AuthorDTO dto = new AuthorDTO(1L, "John Doe");
        when(authorService.getAuthor(1L)).thenReturn(dto);

        AuthorDTO result = authorController.get(1L);

        assertEquals(dto, result);
        verify(authorService).getAuthor(1L);
    }

    @Test
    void getAll_ShouldReturnPagedAuthors() {
        PageRequest request = PageRequest.of(0, 10);
        Page<AuthorDTO> page = new PageImpl<>(List.of(new AuthorDTO(1L, "John Doe")));

        when(authorService.getAllAuthors(request)).thenReturn(page);

        Page<AuthorDTO> result = authorController.getAll(0, 10);

        assertEquals(1, result.getTotalElements());
        verify(authorService).getAllAuthors(request);
    }

    @Test
    void update_ShouldReturnUpdatedAuthor() {
        AuthorDTO input = new AuthorDTO(null, "Updated");
        AuthorDTO updated = new AuthorDTO(1L, "Updated");

        when(authorService.updateAuthor(1L, input)).thenReturn(updated);

        ResponseEntity<AuthorDTO> response = authorController.update(1L, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
        verify(authorService).updateAuthor(1L, input);
    }

    @Test
    void delete_ShouldReturnNoContent() {
        doNothing().when(authorService).deleteAuthor(1L);

        ResponseEntity<Void> response = authorController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authorService).deleteAuthor(1L);
    }
}

