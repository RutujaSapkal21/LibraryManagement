package com.LibraryManagement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.LibraryManagement.Entity.Book;
import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Repository.BookRepository;
import com.LibraryManagement.Service.UserService;

@SpringBootApplication
public class LibraryManagementApplication implements CommandLineRunner{


	@Autowired
	BookRepository bookRepository;

	@Autowired
	UserService userService;

	
	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		insertUser();
		insertBook();

	}

	private void insertUser() {
		
		User user1=new User("1","anil","anil123");
		userService.createUser(user1); // Create user via UserService


		User user2=new User("2","mauli","mauli23");
		userService.createUser(user2); 
		
		User user3=new User("3","soni","soni3");
		userService.createUser(user3); 
		
		System.out.println("Users created successfully at startup.");

	}

	private void insertBook() {
		
		List<Book> l = new ArrayList<>();

		Book b1 = new Book("1", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 3, Book.BookStatus.AVAILABLE);
		l.add(b1);

		Book b2 = new Book("2", "1984", "George Orwell", "Dystopian", 0, Book.BookStatus.BORROWED);
		l.add(b2);

		Book b3 = new Book("3", "To Kill a Mockingbird", "Harper Lee", "Fiction", 5, Book.BookStatus.AVAILABLE);
		l.add(b3);

		Book b4 = new Book("4", "The Catcher in the Rye", "J.D. Salinger", "Fiction", 2, Book.BookStatus.AVAILABLE);
		l.add(b4);

		Book b5 = new Book("5", "Moby Dick", "Herman Melville", "Adventure", 0, Book.BookStatus.BORROWED);
		l.add(b5);
		bookRepository.saveAll(l);
	}

}
