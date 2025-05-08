package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.User;

public interface UserService {
    void registerUser(String username, String password);
    User login(String username, String password);
    User getUserByUsername(String username);
    User updateUser(User user);
} 