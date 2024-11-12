package com.LibraryManagement.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.LibraryManagement.Entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
	 @Query("SELECT b FROM Book b WHERE " +
	           "LOWER(b.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
	           "LOWER(b.author) LIKE LOWER(CONCAT('%', ?2, '%')) OR " +
	           "LOWER(b.genre) LIKE LOWER(CONCAT('%', ?3, '%'))")
	    List<Book> findByTitleContainingOrAuthorContainingOrGenreContaining(String title, String author, String genre);
	
	// @Query("SELECT b FROM Book b WHERE b.user.id = :userId")
	 //List<Book> findBooksByUserId(@Param("userId") String userId);
	
}
