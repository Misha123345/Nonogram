package sk.tuke.gamestudio.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@Transactional
public class UserServiceJPA implements UserService, UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void registerUser(String username, String password) {
        if (getUserByUsernameInternal(username) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(username, passwordEncoder.encode(password));
        entityManager.persist(user);
    }

    @Override
    public User login(String username, String password) {
        User user = getUserByUsernameInternal(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return getUserByUsernameInternal(username);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = getUserByUsernameInternal(user.getUsername());
        
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        
        if (user.getNewUsername() != null && !user.getNewUsername().equals(user.getUsername())) {
            if (getUserByUsernameInternal(user.getNewUsername()) != null) {
                throw new RuntimeException("Username already exists");
            }
            existingUser.setUsername(user.getNewUsername());
        }
        
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        
        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }
        
        if (user.getPasswordHash() != null) {
            existingUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        return entityManager.merge(existingUser);
    }

    private User getUserByUsernameInternal(String username) {
        try {
            return entityManager.createNamedQuery("User.getByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsernameInternal(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                new ArrayList<>()
        );
    }
}
