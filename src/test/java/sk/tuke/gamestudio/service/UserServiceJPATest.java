package sk.tuke.gamestudio.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import sk.tuke.gamestudio.entity.User;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class UserServiceJPATest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testRegisterAndLoginUser() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register user
        userService.registerUser("testuser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Login with valid credentials
        User user = userService.login("testuser", "password123");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testRegisterDuplicateUsername() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register first user
        userService.registerUser("testuser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Try to register with same username
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("testuser", "anotherpassword");
        });
        
        assertTrue(exception.getMessage().contains("Username already exists"));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register user
        userService.registerUser("testuser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Login with invalid credentials
        User user = userService.login("testuser", "wrongpassword");
        assertNull(user);
    }

    @Test
    public void testGetUserByUsername() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register user
        userService.registerUser("testuser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Get user by username
        User user = userService.getUserByUsername("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());

        // Try to get non-existent user
        User nonExistentUser = userService.getUserByUsername("nonexistentuser");
        assertNull(nonExistentUser);
    }

    @Test
    public void testUpdateUserProfile() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register user
        userService.registerUser("testuser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Update user profile
        User updateData = new User();
        updateData.setUsername("testuser");
        updateData.setEmail("test@example.com");
        updateData.setFullName("Test User");
        
        User updatedUser = userService.updateUser(updateData);
        
        assertNotNull(updatedUser);
        assertEquals("testuser", updatedUser.getUsername());
        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("Test User", updatedUser.getFullName());
    }

    @Test
    public void testUpdateUsername() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register users
        userService.registerUser("testuser", "password123");
        userService.registerUser("existinguser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Update username
        User updateData = new User();
        updateData.setUsername("testuser");
        updateData.setNewUsername("newusername");
        
        User updatedUser = userService.updateUser(updateData);
        
        assertNotNull(updatedUser);
        assertEquals("newusername", updatedUser.getUsername());

        // Try update to already existing username
        User userData2 = new User();
        userData2.setUsername("newusername");
        userData2.setNewUsername("existinguser");
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userData2);
        });
        
        assertTrue(exception.getMessage().contains("Username already exists"));
    }

    @Test
    public void testLoadUserByUsername() {
        UserServiceJPA userService = new UserServiceJPA();
        injectEntityManager(userService);

        // Register user
        userService.registerUser("testuser", "password123");
        
        entityManager.flush();
        entityManager.clear();

        // Test UserDetailsService implementation
        UserDetails userDetails = userService.loadUserByUsername("testuser");
        
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        
        // Try to load non-existent user
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistentuser");
        });
        
        assertTrue(exception.getMessage().contains("User not found with username"));
    }
    
    private void injectEntityManager(UserServiceJPA service) {
        try {
            var field = UserServiceJPA.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 