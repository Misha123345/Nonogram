package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.server.security.JwtUtil;
import sk.tuke.gamestudio.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserServiceRest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public static class AuthenticationResponse {
        private final String jwt;
        public AuthenticationResponse(String jwt) { this.jwt = jwt; }
        public String getJwt() { return jwt; }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username and password cannot be empty");
            }
            
            userService.registerUser(username, password);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Username already exists")) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            e.printStackTrace();
            return ResponseEntity.status(500).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password required");
            }
            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Incorrect username or password");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, String> userData) {
        try {
            String originalUsername = userData.get("originalUsername");
            String newUsername = userData.get("username");
            
            if (originalUsername == null || originalUsername.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Original username is required");
            }
            
            User existingUser = userService.getUserByUsername(originalUsername);
            if (existingUser == null) {
                return ResponseEntity.status(404).body("User not found");
            }
            
            User updatedUser = new User();
            updatedUser.setUsername(originalUsername);
            
            if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(originalUsername)) {
                if (userService.getUserByUsername(newUsername) != null) {
                    return ResponseEntity.badRequest().body("Username already exists");
                }
                updatedUser.setNewUsername(newUsername);
            }
            
            String password = userData.get("password");
            if (password != null && !password.trim().isEmpty()) {
                updatedUser.setPasswordHash(password);
            }
            
            String email = userData.get("email");
            if (email != null) {
                updatedUser.setEmail(email);
            }
            
            String fullName = userData.get("fullName");
            if (fullName != null) {
                updatedUser.setFullName(fullName);
            }
            
            User result = userService.updateUser(updatedUser);
            result.setPasswordHash(null);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update user: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                user.setPasswordHash(null);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving user");
        }
    }

    @GetMapping("/exists/{username}")
    public boolean userExists(@PathVariable String username) {
        return userService.getUserByUsername(username) != null;
    }
} 