package com.LibraryManagement.Exception;


public class BookAlreadyAvailableException extends RuntimeException {
	public BookAlreadyAvailableException(String message) {
		
		super(message);
	}
}
