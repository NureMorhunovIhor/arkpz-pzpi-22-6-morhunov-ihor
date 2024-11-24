package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User with ID " + id + " not found."));
    }

    // Add new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Update existing user
    public User updateUser(Integer id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    // Delete user by ID
    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
