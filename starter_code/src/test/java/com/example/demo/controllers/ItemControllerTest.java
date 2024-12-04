package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @InjectMocks
    private ItemController itemController;

    private Item item;
    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testUserLogin");
        user.setPassword("testPassword");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(9.99));
        item.setDescription("This is a test item");
    }

    @Test
    public void getItems_Success() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);
      
        List<Item> items = Arrays.asList(item);

        when(itemRepository.findAll()).thenReturn(items);

        mockMvc.perform(get("/api/item")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'id': 1, 'name': 'Test Item', 'price': 9.99, 'description': 'This is a test item'}]"));
    }

    @Test
    public void getItemById_Success() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/item/1")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id': 1, 'name': 'Test Item', 'price': 9.99, 'description': 'This is a test item'}"));
    }

    @Test
    public void getItemById_NotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/item/1")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByName_Success() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        List<Item> items = Arrays.asList(item);

        when(itemRepository.findByName(anyString())).thenReturn(items);

        mockMvc.perform(get("/api/item/name/Test Item")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'id': 1, 'name': 'Test Item', 'price': 9.99, 'description': 'This is a test item'}]"));
    }

    @Test
    public void getItemsByName_NotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        when(itemRepository.findByName(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/item/name/Test Item")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}