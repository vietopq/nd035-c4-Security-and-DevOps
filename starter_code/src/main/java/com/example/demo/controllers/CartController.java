package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.security.JwtUtil;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
    private JwtUtil jwtUtil;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestHeader("Authorization") String token, @RequestBody ModifyCartRequest request) {
		// Extract the JWT token from the Authorization header
		log.info("Received request to add to cart for user: {}", request.getUsername());
        String jwt = token.substring(7);
        String usernameFromToken = jwtUtil.extractUsername(jwt);

        // Compare the username from the token with the username in the request
        if (!usernameFromToken.equals(request.getUsername())) {
			log.warn("Username from token does not match request username: {} != {}", usernameFromToken, request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
		
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.warn("User not found: {}", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.warn("Item not found: {}", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		log.info("Added item to cart for user: {}", request.getUsername());
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestHeader("Authorization") String token, @RequestBody ModifyCartRequest request) {
		log.info("Received request to remove from cart for user: {}", request.getUsername());
		// Extract the JWT token from the Authorization header
        String jwt = token.substring(7);
        String usernameFromToken = jwtUtil.extractUsername(jwt);

        // Compare the username from the token with the username in the request
        if (!usernameFromToken.equals(request.getUsername())) {
			log.warn("Username from token does not match request username: {} != {}", usernameFromToken, request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.warn("User not found: {}", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.warn("Item not found: {}", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		log.info("Removed item from cart for user: {}", request.getUsername());
		return ResponseEntity.ok(cart);
	}
		
}
