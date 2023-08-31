package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void addNewUserSuccess() throws Exception {
        UserDto userDto = getUserDto(1);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
        verify(userService, times(1)).createUser(any(UserDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void addNewUserShouldReturnBadRequest() throws Exception {
        UserDto userDto = getUserDto(1);
        userDto.setEmail(null);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdSuccessTest() throws Exception {
        UserDto userDto = getUserDto(1);
        when(userService.getUserById(eq(userDto.getId()))).thenReturn(userDto);
        mockMvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
        verify(userService, times(1)).getUserById(eq(userDto.getId()));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllSuccessTest() throws Exception {
        UserDto userDto1 = getUserDto(1);
        UserDto userDto2 = getUserDto(2);
        List<UserDto> userDtoList = Arrays.asList(
                userDto1,
                userDto2
        );
        when(userService.getAllUsers()).thenReturn(userDtoList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(userDto2.getId()));
        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updateTest() throws Exception {
        UserDto userDto = getUserDto(1);
        when(userService.updateUser(eq(userDto.getId()), any(UserDto.class))).thenReturn(userDto);
        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).updateUser(eq(userDto.getId()), any(UserDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById((1));
        verifyNoMoreInteractions(userService);
    }

    private UserDto getUserDto(long id) {
        return UserDto.builder()
                .id(id)
                .name("User " + id)
                .email("Email" + id + "@user.com")
                .build();
    }
}
