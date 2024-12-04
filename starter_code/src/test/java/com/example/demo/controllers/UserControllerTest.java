package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.net.URI;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPassword");
    }

    @Test
    public void registerUser_UsernameTaken() throws Exception {

        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(new User());
        // Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testUser\",\"confirmPassword\":\"testUser\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerUser_PasswordMismatch() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setConfirmPassword("differentpassword");
        mockMvc.perform(post(new URI("/api/user/create"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\",\"confirmPassword\":\"differentpassword\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerUser_Success() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setConfirmPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");

        mockMvc.perform(post(new URI("/api/user/create"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\",\"confirmPassword\":\"password\"}"))
                .andExpect(status().isOk());
    }
}