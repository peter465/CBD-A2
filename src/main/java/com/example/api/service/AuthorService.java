package com.example.api.service;



import com.example.api.dto.AuthorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    AuthorDTO createAuthor(AuthorDTO dto);
    AuthorDTO getAuthor(Long id);
    Page<AuthorDTO> getAllAuthors(Pageable pageable);
    void deleteAuthor(Long id);
	AuthorDTO updateAuthor(Long id, AuthorDTO dto);
}
