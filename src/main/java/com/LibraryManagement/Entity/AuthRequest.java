package com.LibraryManagement.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

	String username;
	String password;
	
	
	public AuthRequest(String username){
		this.username=username;
	}
}
