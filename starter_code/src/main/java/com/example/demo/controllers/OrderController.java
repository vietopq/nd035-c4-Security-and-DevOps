package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.JwtUtil;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
    private JwtUtil jwtUtil;
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@RequestHeader("Authorization") String token, @PathVariable String username) {
		log.info("Received request to submit order for user: {}", username);

		// Extract the JWT token from the Authorization header
        String jwt = token.substring(7);
        String usernameFromToken = jwtUtil.extractUsername(jwt);

        // Compare the username from the token with the username in the request
        if (!usernameFromToken.equals(username)) {
			log.warn("Username from token does not match request username: {} != {}", usernameFromToken, username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.warn("User not found: {}", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("Order submitted for user: {}", username);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@RequestHeader("Authorization") String token, @PathVariable String username) {
		log.info("Received request to get order history for user: {}", username);

		// Extract the JWT token from the Authorization header
        String jwt = token.substring(7);
        String usernameFromToken = jwtUtil.extractUsername(jwt);

        // Compare the username from the token with the username in the request
        if (!usernameFromToken.equals(username)) {
			log.warn("Username from token does not match request username: {} != {}", usernameFromToken, username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.warn("User not found: {}", username);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
