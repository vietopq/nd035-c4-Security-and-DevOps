package com.example.demo.controllers;


import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.net.URI;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;


    @InjectMocks
    private CartController cartController;

    private User user;
    private Cart cart;
    private Item item;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testUserLogin");
        user.setPassword("testPassword");

        cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        user.setCart(cart);

        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setPrice(BigDecimal.valueOf(2.5));
    }

    @Test
    public void addToCart_UserNotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserLogin");
        request.setItemId(1L);
        request.setQuantity(1);

        mockMvc.perform(post("/api/cart/addToCart")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUserLogin\",\"itemId\":1,\"quantity\":1}"))
                .andExpect(status().isNotFound());

        // Add assertion to verify the user was not found
        assertTrue(userRepository.findByUsername("testUserLogin") == null);
    }

    @Test
    public void addToCart_ItemNotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserLogin");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/cart/addToCart")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUserLogin\",\"itemId\":1,\"quantity\":1}")
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isNotFound());

        // Add assertion to verify the item was not found
        assertTrue(itemRepository.findById(1L).isEmpty());
    }

    @Test
    public void addToCart_Success() throws Exception {
        
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserLogin");
        request.setItemId(1L);
        request.setQuantity(1);

        

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(post("/api/cart/addToCart")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUserLogin\",\"itemId\":1,\"quantity\":1}")
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isOk());

        assertTrue(cart.getItems().size() == 1);
    }


    @Test
    public void removeFromCart_UserNotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserLogin");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/cart/removeFromCart")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUserLogin\",\"itemId\":1,\"quantity\":1}")
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isNotFound());

        // Add assertion to verify the user was not found
        assertTrue(userRepository.findByUsername("testUserLogin") == null);
    }

    @Test
    public void removeFromCart_ItemNotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserLogin");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/cart/removeFromCart")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUserLogin\",\"itemId\":1,\"quantity\":1}")
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isNotFound());

        // Add assertion to verify the item was not found
        assertTrue(itemRepository.findById(1L).isEmpty());
    }

    @Test
    public void removeFromCart_Success() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserLogin");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(cart)).thenReturn(cart);

        mockMvc.perform(post("/api/cart/removeFromCart")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUserLogin\",\"itemId\":1,\"quantity\":1}")
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isOk());

        // Add assertion to verify the cart was updated
        assertTrue(cart.getItems().size() == 0);
    }
    
}
