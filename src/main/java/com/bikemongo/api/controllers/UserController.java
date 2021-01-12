package com.bikemongo.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.bikemongo.api.model.Role;
import com.bikemongo.api.model.RoleEnum;
import com.bikemongo.api.model.User;
import com.bikemongo.api.payload.request.LoginRequest;
import com.bikemongo.api.payload.request.RegisterRequest;
import com.bikemongo.api.payload.response.JwtResponse;
import com.bikemongo.api.payload.response.MessageResponse;
import com.bikemongo.api.repositories.RoleRepository;
import com.bikemongo.api.repositories.UserRepository;
import com.bikemongo.api.security.jwt.JwtUtils;
import com.bikemongo.api.security.services.UserDetailsImpl;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping("/users")
	public Iterable<User> getUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping("/user/email/{email}")
	public User getbyEmail(@PathVariable String email) {
		return userRepository.findByEmail(email).get();
	}
	
	// @ResponseBody
	// @PostMapping(value = "/user/register", headers = {
	//             "content-type=application/json" }, consumes = MediaType.APPLICATION_JSON_VALUE)
	// public Boolean addUser(@RequestBody User user) {
	// 	User userByEmail = getbyEmail(user.getEmail());
	// 	if (userByEmail != null) {
	// 		System.out.println("email already in use");
	// 		return false;
	// 	}
	// 	userRepository.save(user);
	// 	return true;

	// }
	
	@DeleteMapping("/user/{email}")
	public String deleteUser(@PathVariable String email) {
		userRepository.deleteById(email);
		return "Deleted";
	}
	
	@PatchMapping("/user/{email}")
	public String updateUser(@PathVariable String email, @RequestBody User user) {
		return "failed";

	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/users/deleteAll")
	public String deleteAll() {
		userRepository.deleteAll();
		return "Deleted All";
	}

	@ResponseBody
	@PostMapping("/user/login")
	public boolean login(@RequestBody User user) {
		User fetchedUser;
		boolean success = false;
		fetchedUser = getbyEmail(user.getEmail());
		System.out.println(user.toString());
		System.out.println(fetchedUser.toString());

		if (fetchedUser.getPassword().equals(user.getPassword()))
			success = true;
		System.out.println(success);
		return success;
	}

	@PostMapping("/user/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles));

	}
	
	@PostMapping("/user/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error! E-Mail ID is already in use"));
		}

		String id = UUID.randomUUID().toString();

		User user = new  User(id, registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getEmail(), encoder.encode(registerRequest.getPassword()), registerRequest.getDateOfBirth());
		
		Set<String> strRoles = registerRequest.getRoles();
		
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
						roles.add(adminRole);
						break;

					default:
						Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
						roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);
		
		return ResponseEntity.ok(new MessageResponse("User " + user.getFirstName() + " " + user.getLastName()
				+ " has successfully registered with e-mail ID " + user.getEmail() + "!"));
		

	}

}
