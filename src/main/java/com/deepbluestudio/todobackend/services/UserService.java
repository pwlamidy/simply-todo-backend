package com.deepbluestudio.todobackend.services;

import com.deepbluestudio.todobackend.models.User;
import com.deepbluestudio.todobackend.repository.UserRepository;
import com.deepbluestudio.todobackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }
}
