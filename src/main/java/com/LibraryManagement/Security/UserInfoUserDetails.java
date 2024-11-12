package com.LibraryManagement.Security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.LibraryManagement.Entity.User;

public class UserInfoUserDetails implements UserDetails {

	private User user;
	
	
    private Collection<? extends GrantedAuthority> authorities;

   
    public UserInfoUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
      
    	this.user=user;
        this.authorities = authorities;
    }

    public UserInfoUserDetails(User userInfo) {
        this.user = userInfo;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    
   
}
