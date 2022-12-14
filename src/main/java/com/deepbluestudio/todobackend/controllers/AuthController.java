package com.deepbluestudio.todobackend.controllers;

import com.deepbluestudio.todobackend.exception.TokenRefreshException;
import com.deepbluestudio.todobackend.models.RefreshToken;
import com.deepbluestudio.todobackend.models.User;
import com.deepbluestudio.todobackend.payload.request.LoginRequest;
import com.deepbluestudio.todobackend.payload.request.SignupRequest;
import com.deepbluestudio.todobackend.payload.request.TokenRefreshRequest;
import com.deepbluestudio.todobackend.payload.response.LoginResponse;
import com.deepbluestudio.todobackend.payload.response.ResponseHandler;
import com.deepbluestudio.todobackend.payload.response.TokenRefreshResponse;
import com.deepbluestudio.todobackend.repository.RefreshTokenRepository;
import com.deepbluestudio.todobackend.repository.UserRepository;
import com.deepbluestudio.todobackend.security.jwt.JwtUtils;
import com.deepbluestudio.todobackend.security.services.RefreshTokenService;
import com.deepbluestudio.todobackend.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new LoginResponse(jwt,
                refreshToken.getToken(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseHandler.generateResponseWithoutData("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseHandler.generateResponseWithoutData("Email is already in use!", HttpStatus.BAD_REQUEST);
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseHandler.generateResponseWithoutData("User registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken currRefreshToken = refreshTokenService.findByToken(requestRefreshToken).orElse(null);

        if (currRefreshToken != null) {
            refreshTokenService.verifyExpiration(currRefreshToken);
            User user = currRefreshToken.getUser();

            String token = jwtUtils.generateTokenFromUsername(user.getUsername());

            // Refresh token rotation: refresh token can only be used once and a new refresh token is generated after use
            refreshTokenRepository.delete(currRefreshToken);
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

            return ResponseEntity.ok(new TokenRefreshResponse(token, newRefreshToken.getToken()));
        } else {
            throw new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!");
        }
    }
}
