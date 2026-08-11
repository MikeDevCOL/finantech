package com.mikedevcol.auth_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.mikedevcol.auth_service.dto.request.UserRequest;
import com.mikedevcol.auth_service.models.Role;
import com.mikedevcol.auth_service.models.User;
import com.mikedevcol.auth_service.repository.RoleRepository;
import com.mikedevcol.auth_service.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
@Transactional
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	void shouldRegisterUserSuccessfully() throws Exception {

		UserRequest userRequest = UserRequest.builder()
				.username("testuser")
				.password("testpassword1234")
				.email("testuser@example.com")
				.build();

		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(userRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.email").value("testuser@example.com"))
				.andExpect(jsonPath("$.id").value("3"))
				.andExpect(jsonPath("$.roleName").value("USER"))
				.andExpect(jsonPath("$.createdAt").exists());

		var userCreated = userRepository.findById(3L).orElse(null);

		assertNotNull(userCreated);
		assertTrue(passwordEncoder.matches("testpassword1234", userCreated.getPassword()));
		assertEquals("testuser", userCreated.getUsername());
		assertEquals("testuser@example.com", userCreated.getEmail());
		assertEquals(3L, userCreated.getId());
		assertEquals("USER", userCreated.getRole().getName());

	}

	@Test
	void shouldReturnValidationErrorsForInvalidInput() throws Exception {
		UserRequest userRequest = UserRequest.builder()
				.username("ab") // Invalid: too short
				.password("123") // Invalid: too short
				.email("invalid-email") // Invalid: not a valid email format
				.build();

		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(userRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation errors"))
				.andExpect(
						jsonPath("$.errors.username[0]").value(
								"Field 'username' must be between 5 and 50 characters"))
				.andExpect(jsonPath("$.errors.password[0]")
						.value("Field 'password' must be between 12 and 100 characters"))
				.andExpect(jsonPath("$.errors.email[0]")
						.value("Field 'email' must be a valid email address"));
	}

	@Test
	void shouldReturnConflictErrorForExistingUsername() throws Exception {

		var existingUser = User.builder()
				.username("existinguser")
				.password(passwordEncoder.encode("validpassword1234"))
				.email("existinguser@example.com")
				.role(Role.builder().id(2L).build());

		userRepository.save(existingUser.build());

		// First, register a user with a specific username
		UserRequest userRequest1 = UserRequest.builder()
				.username("existinguser")
				.password("validpassword1234")
				.email("existinguser@example.com")
				.build();

		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(userRequest1)))
				.andExpect(status().isConflict());

	}

	@Test
	void shouldReturnNotFoundErrorForMissingRole() throws Exception {
		userRepository.deleteAll();
		roleRepository.deleteAll();

		UserRequest userRequest = UserRequest.builder()
				.username("newuser")
				.password("validpassword1234")
				.email("newuser@example.com")
				.build();

		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(userRequest)))
				.andExpect(status().isNotFound());
	}
}
