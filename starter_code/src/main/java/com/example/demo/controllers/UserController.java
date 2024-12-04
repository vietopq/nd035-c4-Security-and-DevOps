package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.JwtUtil;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
    private JwtUtil jwtUtil;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
		log.info("Received request to find user by id: {}", id);

		Optional<User> userDetailOptional = userRepository.findById(id);

		if (!userDetailOptional.isPresent()) {
			log.info("User found: {}", userDetailOptional.get().getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		User userDetail = userDetailOptional.get();

		// Extract the JWT token from the Authorization header
		String jwt = token.substring(7);
		String usernameFromToken = jwtUtil.extractUsername(jwt);

		// Compare the username from the token with the username in the request
		if (!usernameFromToken.equals(userDetail.getUsername())) {
			log.warn("Username from token does not match request username: {} != {}", usernameFromToken, usernameFromToken);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity.ok(userDetail);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@RequestHeader("Authorization") String token, @PathVariable String username) {

		// Extract the JWT token from the Authorization header
        String jwt = token.substring(7);
        String usernameFromToken = jwtUtil.extractUsername(jwt);

        // Compare the username from the token with the username in the request
        if (!usernameFromToken.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.info("Received request to create user: {}", createUserRequest.getUsername());

		if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
			log.warn("Username is already taken: {}", createUserRequest.getUsername());
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (!createUserRequest.isValid()) {
			log.warn("Password must be at least 8 characters long and match the confirmation password for user: {}", createUserRequest.getUsername());
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long and match the confirmation password");
        }

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

		userRepository.save(user);
		log.info("User created successfully: {}", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}

	
}
