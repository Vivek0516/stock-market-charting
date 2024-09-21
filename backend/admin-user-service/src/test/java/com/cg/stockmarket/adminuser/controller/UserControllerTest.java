package com.cg.stockmarket.adminuser.controller;

import com.cg.stockmarket.adminuser.model.User;
import com.cg.stockmarket.adminuser.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        String jsonFilePath = "src/test/resources/user.json";
        user = objectMapper.readValue(new File(jsonFilePath), User.class);
    }

    private String readJsonFile(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void testAddUser_Success() throws Exception {
        User mockedUserResponse = new User(1L, "Vivek", "ROLE_USER", "vivek12@gmail.com", "vivek@123");
        when(userService.addUser(any(User.class))).thenReturn(mockedUserResponse);

        String jsonFilePath = "src/test/resources/user.json";
        String jsonContent = readJsonFile(jsonFilePath);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockedUserResponse.getId()))
                .andExpect(jsonPath("$.name").value(mockedUserResponse.getName()))
                .andExpect(jsonPath("$.role").value(mockedUserResponse.getRole()))
                .andExpect(jsonPath("$.email").value(mockedUserResponse.getEmail()));

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    public void testUpdateUser() throws Exception {
        String jsonFilePath = "src/test/resources/user.json";
        String jsonContent = readJsonFile(jsonFilePath);

        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));

        verify(userService).updateUser(any(Long.class), any(User.class));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()));

        verify(userService).getAllUsers();
    }

    @Test
    public void testGetUsersByRole() throws Exception {
        // Create mock users with roles
        User user1 = new User(1L, "Vivek", "ROLE_USER", "vivek12@gmail.com", "vivek@123");
        User user2 = new User(2L, "Harsh", "ROLE_USER", "harsh@gmail.com", "harsh@123");
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getUsersByRole("ROLE_USER")).thenReturn(users);

        mockMvc.perform(get("/users/role/ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].role").value("ROLE_USER"))
                .andExpect(jsonPath("$[1].role").value("ROLE_USER"));

        verify(userService).getUsersByRole("ROLE_USER");
    }
}
