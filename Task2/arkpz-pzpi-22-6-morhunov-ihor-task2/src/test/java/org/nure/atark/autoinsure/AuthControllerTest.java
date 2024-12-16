package org.nure.atark.autoinsure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.controller.AuthController;
import org.nure.atark.autoinsure.dto.LoginRequest;
import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.service.JwtService;
import org.nure.atark.autoinsure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        Mockito.reset(userService, jwtService);
    }
    @Test
    void testRegisterUser_Success() throws Exception {
        var user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mocked_jwt_token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mocked_jwt_token"));

        verify(userService, times(1)).registerUser(any(User.class));
        verify(jwtService, times(1)).generateToken(user);
    }



    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        var user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        doThrow(new RuntimeException("User with email test@example.com already exists."))
                .when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with email test@example.com already exists."));

        verify(userService, times(1)).registerUser(any(User.class));
        verifyNoMoreInteractions(userService);
    }




    @Test
    void testLoginUser_Success() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        var user = new User();
        user.setId(1);
        user.setEmail("test@example.com");

        when(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mocked_jwt_token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked_jwt_token"));

        verify(userService, times(1)).loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void testLoginUser_InvalidCredentials() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@example.com");
        loginRequest.setPassword("wrongpassword");

        when(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenThrow(new RuntimeException("Invalid email or password."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password."));

        verify(userService, times(1)).loginUser(loginRequest.getEmail(), loginRequest.getPassword());
    }

    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
    }
}
