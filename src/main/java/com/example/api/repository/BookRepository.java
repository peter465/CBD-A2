package com.example.api.repository;



import com.example.api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> ,JpaSpecificationExecutor<Book>
{

    List<Book> findByAuthorId(Long authorId);

    @Query("SELECT b FROM Book b WHERE b.publicationDate BETWEEN :start AND :end")
    List<Book> findByPublicationDateBetween(LocalDate start, LocalDate end);
    
    long countByAuthorId(Long authorId);
}
