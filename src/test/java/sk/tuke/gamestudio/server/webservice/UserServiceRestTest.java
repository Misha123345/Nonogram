package sk.tuke.gamestudio.server.webservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import sk.tuke.gamestudio.server.security.JwtUtil;
import sk.tuke.gamestudio.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceRestTest {
    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceRest userServiceRest;

    private Map<String, String> validCredentials;
    private Map<String, String> invalidCredentials;
    private sk.tuke.gamestudio.entity.User mockUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    public void setup() {
        // Set up test data
        validCredentials = new HashMap<>();
        validCredentials.put("username", "testuser");
        validCredentials.put("password", "password123");

        invalidCredentials = new HashMap<>();
        invalidCredentials.put("username", "");
        invalidCredentials.put("password", "");

        mockUser = new sk.tuke.gamestudio.entity.User("testuser", "hashedpassword");
        mockUser.setEmail("test@example.com");
        mockUser.setFullName("Test User");

        mockUserDetails = new User("testuser", "hashedpassword", new ArrayList<>());
    }

    @Test
    public void testRegisterUserSuccess() {
        // Arrange
        doNothing().when(userService).registerUser(anyString(), anyString());

        // Act
        ResponseEntity<?> response = userServiceRest.registerUser(validCredentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
        verify(userService).registerUser("testuser", "password123");
    }

    @Test
    public void testRegisterUserEmptyCredentials() {
        // Act
        ResponseEntity<?> response = userServiceRest.registerUser(invalidCredentials);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username and password cannot be empty", response.getBody());
        verify(userService, never()).registerUser(anyString(), anyString());
    }

    @Test
    public void testRegisterUserDuplicate() {
        // Arrange
        doThrow(new RuntimeException("Username already exists")).when(userService).registerUser(anyString(), anyString());

        // Act
        ResponseEntity<?> response = userServiceRest.registerUser(validCredentials);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    public void testLoginSuccess() {
        // Arrange
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(mockUserDetails)).thenReturn("test.jwt.token");

        // Act
        ResponseEntity<?> response = userServiceRest.login(validCredentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserServiceRest.AuthenticationResponse);
        assertEquals("test.jwt.token", ((UserServiceRest.AuthenticationResponse) response.getBody()).getJwt());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).generateToken(mockUserDetails);
    }

    @Test
    public void testLoginBadCredentials() {
        // Arrange
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act
        ResponseEntity<?> response = userServiceRest.login(validCredentials);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody());
    }

    @Test
    public void testGetUserByUsername() {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(mockUser);

        // Act
        ResponseEntity<?> response = userServiceRest.getUserByUsername("testuser");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof sk.tuke.gamestudio.entity.User);
        
        sk.tuke.gamestudio.entity.User returnedUser = (sk.tuke.gamestudio.entity.User) response.getBody();
        assertEquals("testuser", returnedUser.getUsername());
        assertNull(returnedUser.getPasswordHash()); // Password should be removed
        assertEquals("test@example.com", returnedUser.getEmail());
        assertEquals("Test User", returnedUser.getFullName());
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        // Arrange
        when(userService.getUserByUsername("nonexistent")).thenReturn(null);

        // Act
        ResponseEntity<?> response = userServiceRest.getUserByUsername("nonexistent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUserExists() {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(mockUser);
        when(userService.getUserByUsername("nonexistent")).thenReturn(null);

        // Act
        boolean existsResult = userServiceRest.userExists("testuser");
        boolean notExistsResult = userServiceRest.userExists("nonexistent");

        // Assert
        assertTrue(existsResult);
        assertFalse(notExistsResult);
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        Map<String, String> updateData = new HashMap<>();
        updateData.put("originalUsername", "testuser");
        updateData.put("username", "newusername");
        updateData.put("email", "newemail@example.com");
        updateData.put("fullName", "New Name");
        
        when(userService.getUserByUsername("testuser")).thenReturn(mockUser);
        when(userService.getUserByUsername("newusername")).thenReturn(null); // New username doesn't exist
        when(userService.updateUser(any(sk.tuke.gamestudio.entity.User.class))).thenReturn(mockUser);
        
        // Mock the returned user with updated values
        mockUser.setUsername("newusername");
        mockUser.setEmail("newemail@example.com");
        mockUser.setFullName("New Name");

        // Act
        ResponseEntity<?> response = userServiceRest.updateUser(updateData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof sk.tuke.gamestudio.entity.User);
        
        sk.tuke.gamestudio.entity.User returnedUser = (sk.tuke.gamestudio.entity.User) response.getBody();
        assertEquals("newusername", returnedUser.getUsername());
        assertNull(returnedUser.getPasswordHash()); // Password should be removed
        assertEquals("newemail@example.com", returnedUser.getEmail());
        assertEquals("New Name", returnedUser.getFullName());
    }
} 