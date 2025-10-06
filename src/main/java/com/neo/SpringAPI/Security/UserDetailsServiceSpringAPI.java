package com.neo.SpringAPI.Security;

import com.neo.SpringAPI.Repositories.UserRepository;
import com.neo.SpringAPI.Entities.User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Service
public class UserDetailsServiceSpringAPI implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceSpringAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username);
        if (u == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        // Map role (moderator/publisher) to ROLE_*
        String role = u.getRole();
        if (role == null) {
            role = "publisher"; // default
        }
        String springRole = "ROLE_" + role.toUpperCase();
        UserBuilder builder = org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(springRole)));
        return builder.build();
    }
}
