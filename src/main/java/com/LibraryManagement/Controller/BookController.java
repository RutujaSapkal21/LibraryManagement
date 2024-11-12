package com.LibraryManagement.Controller;



import java.util.Collections;

import java.util.List;
import java.util.Map;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LibraryManagement.Entity.Book;
import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Exception.UserNotFoundException;
import com.LibraryManagement.Repository.BookRepository;
import com.LibraryManagement.Repository.UserRepository;
import com.LibraryManagement.Service.BookService;
import com.LibraryManagement.jwt.JwtService;



@RestController
@RequestMapping("/api")
public class BookController {

	@Autowired
	BookService bookService;
	
	@Autowired
	BookRepository bookRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtService jwtService;

	@Autowired
	AuthenticationManager authManager;

	@GetMapping("/books/search")
	public ResponseEntity<List<Book>> searchBook(@RequestParam(required = false) String title,
			@RequestParam(required = false) String author, @RequestParam(required = false) String genre) {
		 	
		List<Book> book = bookService.getbook(title, author, genre);
		if (book.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(book, HttpStatus.OK);

	}

	@PutMapping("/borrow")
	public ResponseEntity<?> borrowBook(@RequestParam("userId") String userId,
			@RequestParam("bookId") String bookId) {
		// Get the username of the authenticated user (from the JWT)
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String authenticatedUsername = authentication.getName();
	    
	    User user=userRepository.findById(userId).get();
	    
	 // Check if the authenticated user matches the userId in the URL path
	    if (!authenticatedUsername.equals(user.getUsername())) {
	        throw new AccessDeniedException("You cannot access the Services of another user");
	    }
		
		String dueDate = bookService.borrowBook(bookId, userId);

		if (dueDate != null) {
			return ResponseEntity.ok("Book borrowed successfully. Due date: " + dueDate);

		} else {
			return ResponseEntity.badRequest().body("Book not available.");
		}

	}

	@GetMapping("/{userId}/borrowing-history")
	public ResponseEntity<List<Map<String, Object>>> getBorrowingHistory(@PathVariable String userId) {
	
		try {
			
			// Get the username of the authenticated user (from the JWT)
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    String authenticatedUsername = authentication.getName();
		    
		    User user=userRepository.findById(userId).get();
		    
		 // Check if the authenticated user matches the userId in the URL path
		    if (!authenticatedUsername.equals(user.getUsername())) {
		        throw new AccessDeniedException("You cannot access the borrowing history of another user");
		    }

			
		List<Map<String, Object>> borrowingHistory = bookService.getBorrowingHistory(userId);
			return ResponseEntity.ok(borrowingHistory);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
		}
	}

	@PutMapping("/return")
	public ResponseEntity<String> returnBook(@RequestParam("bookId") String bookId,
			@RequestParam("userId") String userId) {
		try {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    String authenticatedUsername = authentication.getName();
		    
		    User user=userRepository.findById(userId).get();
		    
		    // Check if the authenticated user matches the userId in the URL path
		    if (!authenticatedUsername.equals(user.getUsername())) {
		        throw new AccessDeniedException("You cannot access the Services of another user");
		    }

			String message = bookService.returnBook(userId, bookId);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/authenticate")
	public String generateJWTToken(@RequestParam("username") String username,
			@RequestParam("password") String password) {

	
		Authentication authentication = authManager.authenticate(
						new UsernamePasswordAuthenticationToken(username,password));
		
		
		 SecurityContextHolder.getContext().setAuthentication(authentication);
		if (authentication.isAuthenticated()) {
		//	return jwtService.generateToken(username);
			 String token = jwtService.generateToken(username);
		        System.out.println("Generated Token: " + token); // Log it for debugging
		        return token;

		} else {

			throw new UsernameNotFoundException("Invalid user request!!!");

		}
		
	}
	
	
}
