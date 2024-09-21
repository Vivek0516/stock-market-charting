package com.cg.stockmarket.adminuser.service;

import com.cg.stockmarket.adminuser.exception.UserNotFoundException;
import com.cg.stockmarket.adminuser.model.User;
import com.cg.stockmarket.adminuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class that handles the business logic for managing users in the Admin User Service.
 * This class provides methods for CRUD operations on users.
 *
 * It communicates with the {@link UserRepository} to perform these operations.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of {@link User} objects.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the {@link User} object if found.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the {@link User} object to be added.
     * @return the saved {@link User} object.
     */
    public User addUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates an existing user by their ID.
     *
     * @param id the ID of the user to update.
     * @param user the {@link User} object containing the updated details.
     * @return the updated {@link User} object.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
    public User updateUser(Long id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            return userRepository.save(user);
        } //directly throwing message without creating custom exception
        throw new UserNotFoundException("User not found with id: " + id);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    /**
     * Retrieves a list of users by their role.
     *
     * @param role the role of the users to retrieve.
     * @return a list of {@link User} objects that match the specified role.
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
}
