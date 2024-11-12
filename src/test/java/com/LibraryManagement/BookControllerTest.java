package com.LibraryManagement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;



import com.LibraryManagement.Controller.BookController;
import com.LibraryManagement.Entity.Book;
import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Repository.BookRepository;
import com.LibraryManagement.Repository.BorrowedBookRepository;
import com.LibraryManagement.Repository.UserRepository;
import com.LibraryManagement.Service.BookService;
import com.LibraryManagement.Service.UserService;
import com.LibraryManagement.jwt.JwtAuthenticationFilter;
import com.LibraryManagement.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

	@MockBean
	UserRepository userRepository;
	@MockBean
	BookRepository bookRepository;

	@MockBean
	BorrowedBookRepository borrowedBookRepository;

	@MockBean
	BookService bookService;
	@MockBean
	UserService userService;

	@Autowired
	MockMvc mockMvc;

	@MockBean
	JwtService jwtService;

	@MockBean
	AuthenticationManager authenticationManager;

	@MockBean
	JwtAuthenticationFilter jwtFilter;

	@Autowired
	ObjectMapper mapper;

	@MockBean
	Authentication authentication;

	@BeforeEach
	public void setup() {
		// Set up authentication context
		String authenticatedUsername = "user1";
		Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUsername, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		
		
	}

	@Test
	void testSearchBookByTitle() throws Exception {
		// Arrange
		Book book1 = Book.builder().bookId("1").title("The Lord of the Rings").author("R.R.").availableCopies(2)
				.genre("fiction").build();

		List<Book> books = Arrays.asList(book1);

		when(bookService.getbook("The Lord of the Rings", null, null)).thenReturn(books);
		ResultActions response = mockMvc.perform(get("/api/books/search").param("title", "The Lord of the Rings"));

		MockHttpServletResponse res = response.andReturn().getResponse();

		Book[] booklist = mapper.readValue(res.getContentAsString(), Book[].class);

		assertEquals(booklist[0].getTitle(), book1.getTitle());

	}

	@Test
	
	void testBorrowBook() throws Exception {
		// Arrange: Setup mock user and mock bookService
		String userId = "1";
		String bookId = "book1";
		String dueDate = "2024-11-20";

		User mockUser = new User();
		mockUser.setUserId(userId);
		mockUser.setUsername("user1"); // Make sure the authenticated user matches

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		when(bookService.borrowBook(bookId, userId)).thenReturn(dueDate);

		// Perform PUT request
		ResultActions response = mockMvc.perform(put("/api/borrow").param("userId", userId).param("bookId", bookId))
				.andExpect(status().isOk())
				.andExpect(content().string("Book borrowed successfully. Due date: " + dueDate)); // Assert success

	}
	

	@Test
	public void testReturnBook_Success() throws Exception {
		String bookId = "1";
		String userId = "1";

		User mockUser = new User();
		mockUser.setUserId(userId);
		mockUser.setUsername("user1"); // Same as the authenticated username
		// Mock the repository and service methods
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(bookService.returnBook(userId, bookId)).thenReturn("Book returned successfully");

		// Perform the PUT request to return the book
		mockMvc.perform(put("/api/return").param("bookId", bookId).param("userId", userId)).andExpect(status().isOk())
				.andExpect(content().string("Book returned successfully"));

	}

	@Test
	public void testGenerateJWTToken() throws Exception {
		String username = "user1";
		String password = "password";
		String token = "generated-jwt-token";

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);

		when(authentication.isAuthenticated()).thenReturn(true);

		// Mock JWT generation
		when(jwtService.generateToken(username)).thenReturn(token);

		// Perform POST request to the /authenticate endpoint
		mockMvc.perform(post("/api/authenticate").param("username", username).param("password", password))
				.andExpect(status().isOk()).andExpect(content().string(token));

	}



}
