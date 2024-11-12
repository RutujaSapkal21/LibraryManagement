package com.LibraryManagement.Security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.LibraryManagement.Entity.User;
import com.LibraryManagement.Repository.UserRepository;

@Service
public class UserInfoUserDetailsService implements UserDetailsService{

	
	
	
	
	
		@Autowired
		UserRepository userInfoRepository;
		
		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			Optional<User> userInfo=userInfoRepository.findByUsername(username);
			 if (userInfo == null) {
		            throw new UsernameNotFoundException("User not found: " + username);
		        }
			/* 
			 return new User(
		                userInfo.getUsername(),
		                userInfo.getPassword(),
		                // Set authorities based on user roles or permissions
		                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
		        );*/
			return userInfo.map(UserInfoUserDetails::new).orElseThrow(()->new UsernameNotFoundException("User not found"+username));
		}

	 
	}
	
	