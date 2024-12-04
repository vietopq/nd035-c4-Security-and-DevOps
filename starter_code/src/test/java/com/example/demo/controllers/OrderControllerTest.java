package com.example.demo.controllers;


import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @InjectMocks
    private OrderController orderController;

    private User user;
    private Cart cart;
    private Item item;
    private UserOrder order;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testUserLogin");
        user.setPassword("testPassword");

        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setPrice(BigDecimal.valueOf(2.5));

        cart = new Cart();
        cart.setId(1L);
        cart.setItems(Collections.singletonList(item));
        cart.setUser(user);
        user.setCart(cart);

        order = new UserOrder();
        order.setId(1L);
        order.setUser(user);
        order.setItems(Collections.singletonList(item));
        order.setTotal(BigDecimal.valueOf(2.5));
    }

    @Test
    public void submitOrder_UserNotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/order/submit/testUserLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isNotFound());

        // Add assertion to verify the user was not found
        assertTrue(userRepository.findByUsername("testUserLogin") == null);
    }

    @Test
    public void getOrdersForUser_UserNotFound() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/order/history/testUserLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isNotFound());

        // Add assertion to verify the user was not found
        assertTrue(userRepository.findByUsername("testUserLogin") == null);
    }

    @Test
    public void getOrdersForUser_Success() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtUtil.extractUsername(anyString())).thenReturn("testUserLogin");
        when(jwtUtil.validateToken(anyString(), any())).thenReturn(true);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(order));

        mockMvc.perform(get("/api/order/history/testUserLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isOk());

        // Add assertion to verify the order history was retrieved
        assertTrue(orderRepository.findByUser(user).contains(order));
    }
}