package com.LibraryManagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.LibraryManagement.Entity.Book;
import com.LibraryManagement.Entity.Book.BookStatus;
import com.LibraryManagement.Entity.BorrowedBook;
import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Exception.BookNotFoundException;
import com.LibraryManagement.Repository.BookRepository;
import com.LibraryManagement.Repository.BorrowedBookRepository;
import com.LibraryManagement.Repository.UserRepository;
import com.LibraryManagement.Service.BookService;
import com.LibraryManagement.Service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class BookServiceTest {

	@MockBean
	BookRepository bookRepo;

	@MockBean
	UserRepository userRepo;

	@MockBean
	BorrowedBookRepository borrowedBookRepo;

	@InjectMocks
	BookService bookService; // For real service

	@MockBean
	UserService userService;

	@Test
	public void testGetBooks() {
		 // Arrange
	    Book mockBook1 = new Book("1", "Book1", "R.R", "Fiction", 5, BookStatus.AVAILABLE);
	    Book mockBook2 = new Book("2", "Book2", "A.B", "Fiction", 1, BookStatus.AVAILABLE);
	    List<Book> expectedbooks = Arrays.asList(mockBook1, mockBook2);
	    // Mock repository call
	  
	    when(bookRepo.findByTitleContainingOrAuthorContainingOrGenreContaining(null, null, "Fiction"))
	            .thenReturn(expectedbooks);

	    List<Book> result = bookService.getbook(null, null, "Fiction");

	    // Assert
	    assertEquals(2, result.size());
	    assertEquals("Book1", result.get(0).getTitle()); //assert for check sorted by title
	    assertEquals("Book2", result.get(1).getTitle());
	    
	    
	    // Mock the repository method to return an empty list
        when(bookRepo.findByTitleContainingOrAuthorContainingOrGenreContaining("Book3", null, null))
                .thenReturn(List.of());

        // Call the method under test and verify the exception is thrown
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.getbook("Book3", null, null);
        });

        assertEquals("No books found for the given search criteria.", exception.getMessage());
	}

	@Test
	void testBorrowBook() {
		// Mocking user
		User mockUser = new User("1", "Rani", "rani123@");

		// Mocking book
		Book mockBook = new Book("1", "Book One", "R.R", "Fiction", 5, BookStatus.AVAILABLE);

		// Mocking the user and book repository behavior
		when(userRepo.findById("1")).thenReturn(Optional.of(mockUser));
		when(bookRepo.findById("1")).thenReturn(Optional.of(mockBook));

		// Calling the borrowBook method
		String dueDate = bookService.borrowBook("1", "1");

		// Assert that due date is returned correctly
		assertNotNull(dueDate);
		assertEquals(LocalDate.now().plusDays(14).toString(), dueDate);

		// Verify that the book's available copies and status were updated
		assertEquals(4, mockBook.getAvailableCopies());
		

	}

	@Test
	void testGetBorrowingHistory_Success() {
		// Mocking user
		User mockUser = new User("1", "Rani", "rani123@");

		// Mocking book
		Book mockBook = new Book("1", "Book One", "R.R", "Fiction", 5, BookStatus.BORROWED);

		// Mocking borrowed book record
		BorrowedBook borrowedBook = new BorrowedBook();
		borrowedBook.setBook(mockBook);
		borrowedBook.setUser(mockUser); // Use mockUser here
		borrowedBook.setBorrowDate(LocalDate.now());
		borrowedBook.setDueDate(LocalDate.now().plusDays(14));
		borrowedBook.setReturnDate(null); // Book has not been returned yet

		List<BorrowedBook> borrowedBooks = Arrays.asList(borrowedBook);

		// Mocking repository behaviors
		when(userRepo.findById("1")).thenReturn(Optional.of(mockUser));
		when(borrowedBookRepo.findByUserId("1")).thenReturn(borrowedBooks);

		// Calling the method to fetch borrowing history
		List<Map<String, Object>> history = bookService.getBorrowingHistory("1");

		// Assertions
		assertNotNull(history);
		assertEquals(1, history.size());
		Map<String, Object> record = history.get(0);

		// Assertions should match mock data
		assertEquals("1", record.get("bookId"));
		assertEquals("Book One", record.get("title")); // Match the mockBook title
		assertEquals(LocalDate.now().toString(), record.get("borrowDate")); // Match the borrowDate
		assertEquals(LocalDate.now().plusDays(14).toString(), record.get("dueDate")); // Match the dueDate
		assertNull(record.get("returnDate")); // Return date is null
	}

	@Test
	void testReturnBook() {

		// Mocking user
		User mockUser = new User("1", "Rani", "rani123@");

		// Mocking book
		Book mockBook = new Book("1", "Book One", "R.R", "Fiction", 5, BookStatus.AVAILABLE);

		// Mocking borrowed book record
		BorrowedBook borrowedBook = new BorrowedBook();
		borrowedBook.setBook(mockBook);
		borrowedBook.setUser(mockUser);
		borrowedBook.setBorrowDate(LocalDate.now());
		borrowedBook.setDueDate(LocalDate.now().plusDays(14));
		borrowedBook.setReturnDate(null);

		when(userRepo.findById("1")).thenReturn(Optional.of(mockUser));
		when(bookRepo.findById("1")).thenReturn(Optional.of(mockBook));
		when(borrowedBookRepo.findByUser_UserIdAndBook_BookId("1", "1")).thenReturn(Optional.of(borrowedBook));

		String result = bookService.returnBook("1", "1");

		// Assert
		assertEquals("Book returned successfully.", result);
		assertEquals(BookStatus.AVAILABLE, mockBook.getStatus());
		assertEquals(6, mockBook.getAvailableCopies());
		assertNotNull(borrowedBook.getReturnDate());

	}

}