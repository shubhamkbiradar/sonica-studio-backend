package com.project.sonica.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("securityUserService")
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User registerUser(RegisterRequest request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword())); // hash password
		user.setRole(request.getRole());

		return userRepository.save(user);
	}
	
	public void resetPassword(String email, String newPassword) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found."));
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);
	}
}
