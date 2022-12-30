package com.deepbluestudio.todobackend;

import com.deepbluestudio.todobackend.models.RefreshToken;
import com.deepbluestudio.todobackend.models.User;
import com.deepbluestudio.todobackend.repository.UserRepository;
import com.deepbluestudio.todobackend.security.jwt.JwtUtils;
import com.deepbluestudio.todobackend.security.services.RefreshTokenService;
import com.deepbluestudio.todobackend.security.services.UserDetailsImpl;
import com.deepbluestudio.todobackend.security.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    RefreshTokenService refreshTokenService;

    private static User testUser;
    private static String testUserJsonString;
    private static UserDetailsImpl testUserDetails;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        testUser = new User("test", "test@test.com", "test");

        ObjectMapper objectMapper = new ObjectMapper();
        testUserJsonString = objectMapper.writeValueAsString(testUser);

        testUserDetails = new UserDetailsImpl(testUser.getId(), testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
    }


    @Test
    public void newUserSignUpSuccess() throws Exception {
        when(userRepository.existsByUsername(Mockito.any(String.class))).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(Mockito.any(String.class))).thenReturn(Boolean.FALSE);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .content(testUserJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void existingUsernameSignupFail() throws Exception {
        when(userRepository.existsByUsername(Mockito.any(String.class))).thenReturn(Boolean.TRUE);
        when(userRepository.existsByEmail(Mockito.any(String.class))).thenReturn(Boolean.FALSE);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .content(testUserJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void existingEmailSignupFail() throws Exception {
        when(userRepository.existsByUsername(Mockito.any(String.class))).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(Mockito.any(String.class))).thenReturn(Boolean.TRUE);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .content(testUserJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void existingUserSignInSuccess() throws Exception {
        String testJwtToken = "123";

        RefreshToken testRefreshToken = new RefreshToken();
        testRefreshToken.setToken("test");

        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(true);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);

        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userDetailsServiceImpl.loadUserByUsername(eq(testUser.getEmail()))).thenReturn(testUserDetails);
        when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn(testJwtToken);
        when(refreshTokenService.createRefreshToken(Mockito.any())).thenReturn(testRefreshToken);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .content(testUserJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.accessToken").value(testJwtToken));
    }

    @Test
    public void invalidUserSignInFail() throws Exception {
        Authentication authentication = mock(Authentication.class);
        authentication.setAuthenticated(false);

        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userDetailsServiceImpl.loadUserByUsername(eq(testUser.getEmail()))).thenReturn(null);
        when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .content(testUserJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    public void refreshJwtTokenSuccess() throws Exception {
        String testJwtToken = "123";

        Map<String, String> testReqBody = new HashMap<>() {{
            put("refreshToken", "test");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String refreshTokenString = objectMapper.writeValueAsString(testReqBody);

        RefreshToken testRefreshToken = new RefreshToken();
        testRefreshToken.setToken("test");
        testRefreshToken.setUser(testUser);

        when(refreshTokenService.findByToken(Mockito.any(String.class))).thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenService.verifyExpiration(Mockito.any(RefreshToken.class))).thenReturn(testRefreshToken);
        when(jwtUtils.generateTokenFromUsername(Mockito.any(String.class))).thenReturn(testJwtToken);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh-token")
                        .content(refreshTokenString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.accessToken").value(testJwtToken));
    }
}
