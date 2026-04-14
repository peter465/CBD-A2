package com.example.api.repository;

import com.example.api.entity.Author;
import com.example.api.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findByAuthorId_shouldReturnBooksForAuthor() {
        Author author = new Author();
        author.setName("John Doe");
        author.setEmail("john@example.com");
        entityManager.persist(author);

        Book book1 = new Book();
        book1.setTitle("Book A");
        book1.setPublicationDate(LocalDate.now());
        book1.setAuthor(author);
        entityManager.persist(book1);

        Book book2 = new Book();
        book2.setTitle("Book B");
        book2.setPublicationDate(LocalDate.now());
        book2.setAuthor(author);
        entityManager.persist(book2);

        entityManager.flush();

        List<Book> found = bookRepository.findByAuthorId(author.getId());
        assertThat(found).hasSize(2);
    }

    @Test
    void countByAuthorId_shouldReturnCorrectCount() {
        Author author = new Author();
        author.setName("Jane Smith");
        author.setEmail("jane@example.com");
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Only One");
        book.setPublicationDate(LocalDate.now());
        book.setAuthor(author);
        entityManager.persist(book);

        entityManager.flush();

        long count = bookRepository.countByAuthorId(author.getId());
        assertThat(count).isEqualTo(1);
    }
}