package com.LibraryManagement.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LibraryManagement.Entity.Book;
import com.LibraryManagement.Entity.Book.BookStatus;
import com.LibraryManagement.Entity.BorrowedBook;
import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Exception.BookNotAvailableException;
import com.LibraryManagement.Exception.BookNotFoundException;
import com.LibraryManagement.Exception.UserNotFoundException;
import com.LibraryManagement.Repository.BookRepository;
import com.LibraryManagement.Repository.BorrowedBookRepository;
import com.LibraryManagement.Repository.UserRepository;

@Service
public class BookService {

	@Autowired
	BookRepository bookRepo;

	@Autowired
	BorrowedBookRepository borrowedBookRepo;
	
	@Autowired
	UserRepository userRepo;

	public List<Book> getbook(String title, String author, String genre) {
		List<Book> books = bookRepo.findByTitleContainingOrAuthorContainingOrGenreContaining(title,author,genre);

		if (books.isEmpty()) {
			throw new BookNotFoundException("No books found for the given search criteria.");
		}

		books.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
		return books;
	}

	public String borrowBook(String bookId, String userId) {	
		User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));
		Book book = bookRepo.findById(bookId).orElseThrow(() -> new BookNotAvailableException("Book not found."));
		if (book.getAvailableCopies() <= 0 || "BORROWED".equals(book.getStatus())) {
			throw new BookNotAvailableException("Book is not available for borrowing.");
		}
		
		LocalDate dueDate = LocalDate.now().plusDays(14);
		

		book.setAvailableCopies(book.getAvailableCopies() - 1);
	
		if((book.getAvailableCopies())==0) {
		book.setStatus(BookStatus.BORROWED);
		}else {
			book.setStatus(BookStatus.AVAILABLE);
		}
	//	book.setUser(user); 
		userRepo.save(user);
		bookRepo.save(book);
		

		BorrowedBook borrowing = new BorrowedBook();
		
        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(dueDate);  
       
   //     borrowing.setBookStatus(BookStatus.BORROWED);

        borrowedBookRepo.save(borrowing);
		
		
		return dueDate.toString();
	}
	
	
	
	 public List<Map<String, Object>> getBorrowingHistory(String userId)  {
	        // Fetch the user
	        User user = userRepo.findById(userId) .orElseThrow(() -> new UserNotFoundException("User not found"));

	        // Fetch borrowings related to the user
	        List<BorrowedBook> borrowings = borrowedBookRepo.findByUserId(userId);

	        List<Map<String, Object>> borrowingHistory = new ArrayList<>();
	        for (BorrowedBook borrowing : borrowings) {
	            Map<String, Object> historyMap = new HashMap<>();
	            Book book = borrowing.getBook();

	            // Prepare the data in the required format
	            historyMap.put("bookId", book.getBookId());
	            historyMap.put("title", book.getTitle());
	            historyMap.put("borrowDate", borrowing.getBorrowDate().toString());
	            historyMap.put("dueDate", borrowing.getDueDate().toString());
	            historyMap.put("returnDate", borrowing.getReturnDate() != null ? borrowing.getReturnDate().toString() : null);

	            borrowingHistory.add(historyMap);
	        }	

	        return borrowingHistory;
	    }
	
	 public String returnBook(String userId, String bookId)  {
	        // Validate if user exists
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found"));

	        // Validate if the book exists
	        Book book = bookRepo.findById(bookId)
	                .orElseThrow(() -> new BookNotFoundException("Book not found"));

	        // Find the borrowing record of this book for the user
	        BorrowedBook borrowing = borrowedBookRepo.findByUser_UserIdAndBook_BookId(userId,bookId).get();
	        
	       
	        book.setStatus(BookStatus.AVAILABLE);
	        book.setAvailableCopies(book.getAvailableCopies() + 1);
	        bookRepo.save(book);

	       
	        borrowing.setReturnDate(LocalDate.now());
	        borrowedBookRepo.save(borrowing);

	        return "Book returned successfully.";
	    }
	 
	 
	 
	
}