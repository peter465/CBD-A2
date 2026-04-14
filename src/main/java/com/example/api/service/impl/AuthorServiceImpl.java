package com.example.api.service.impl;



import com.example.api.dto.AuthorDTO;
import com.example.api.entity.Author;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.AuthorRepository;
import com.example.api.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    private AuthorDTO toDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setEmail(author.getEmail());
        dto.setTotalBooks(author.getBooks().size());
        return dto;
    }

    private Author fromDTO(AuthorDTO dto) {
        Author a = new Author();
        a.setName(dto.getName());
        a.setEmail(dto.getEmail());
        return a;
    }

    @Override
    public AuthorDTO createAuthor(AuthorDTO dto) {
        Author author = fromDTO(dto);
        Author saved = authorRepository.save(author);
        return toDTO(saved);
    }

    @Override
    public AuthorDTO getAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + id));
        return toDTO(author);
    }

    @Override
    public Page<AuthorDTO> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + id));
        authorRepository.delete(author); // cascade deletes books
    }
}
