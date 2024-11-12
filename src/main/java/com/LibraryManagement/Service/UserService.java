package com.LibraryManagement.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.LibraryManagement.Entity.AuthRequest;
import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Repository.UserRepository;

@Service
public class UserService {
	@Autowired
	 UserRepository userRepository;
	@Autowired
	 PasswordEncoder passwordEncoder;

	

	public AuthRequest createUser(User userDTO) {
		// Validate user input
		 // Check if the username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists.");
        }

		// Encode the password before saving the user
		String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
		userDTO.setPassword(encodedPassword);

		User savedUser = userRepository.save(userDTO);

		return new AuthRequest(savedUser.getUsername());
	}

	
	public Optional<User> login(String username) {
		// ... authentication logic
		return userRepository.findByUsername(username);
	}

	
}