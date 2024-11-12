package com.LibraryManagement.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.LibraryManagement.Security.UserInfoUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired

	JwtService jwtService;

	@Autowired

	UserInfoUserDetailsService userDetailsService;

	@Override

	protected void doFilterInternal(HttpServletRequest request,

			HttpServletResponse response, FilterChain filterChain)

			throws ServletException, IOException {

		// Token will transfer in the header called Authorization "Bearer token"

		String authHeader = request.getHeader("Authorization"); // Bearer
																// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSYW0iLCJpYXQiOjE3Mjk3NDgzOTUsImV4cCI6MTcyOTc1MDE5NX0.CyE5SRkhh90cwqxjWuwtDQPdApVe5VeTvyPeswLBcMI"

		String token = null;

		String username = null;

		// Extract the token which contains username

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			
			token = authHeader.substring(7);

			username = jwtService.extractUsername(token);
			 System.out.println("Token is valid. Username: " + username);
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtService.validateToken(token, userDetails)) {

				UsernamePasswordAuthenticationToken authToken = new

				UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);

			}

		}

		filterChain.doFilter(request, response);

	}

}