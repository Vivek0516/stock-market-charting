package com.cg.stockmarket.adminuser.service;

import com.cg.stockmarket.adminuser.exception.UserNotFoundException;
import com.cg.stockmarket.adminuser.model.User;
import com.cg.stockmarket.adminuser.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "Vivek", "ROLE_USER", "vivek12@gmail.com", "vivek@123");
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("Vivek", foundUser.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.addUser(user);

        assertNotNull(savedUser);
        assertEquals("Vivek", savedUser.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getAllUsers();

        assertEquals(1, foundUsers.size());
        assertEquals("Vivek", foundUsers.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUsersByRole() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findByRole("ROLE_USER")).thenReturn(users);

        List<User> foundUsers = userService.getUsersByRole("ROLE_USER");

        assertEquals(1, foundUsers.size());
        assertEquals("Vivek", foundUsers.get(0).getName());
        verify(userRepository, times(1)).findByRole("ROLE_USER");
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("User not found with id: 1", thrown.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testUpdateUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, user);

        assertNotNull(updatedUser);
        assertEquals("Vivek", updatedUser.getName());

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(1L, user);
        });

        assertEquals("User not found with id: 1", thrown.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
