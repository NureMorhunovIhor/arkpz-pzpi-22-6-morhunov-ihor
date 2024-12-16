package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User with ID " + id + " not found."));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists.");
        }
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }
        return user;
    }
    public User updateUser(Integer id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
