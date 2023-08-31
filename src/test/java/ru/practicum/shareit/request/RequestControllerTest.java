package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BaseConstants;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequestSuccessTest() throws Exception {
        long userId = 1;
        ItemRequestAddDto itemRequestAddDto = getRequest();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1)
                .description("Test description")
                .requestor(getRequester())
                .build();
        when(itemRequestService.createRequest(any(ItemRequestAddDto.class), eq(userId)))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest));
        mockMvc.perform(post("/requests")
                        .header(BaseConstants.HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemRequestAddDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemRequestService, times(1)).createRequest(any(ItemRequestAddDto.class), eq(userId));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getRequestByIdSuccessTest() throws Exception {
        long userId = 1;
        ItemRequestAddDto itemRequestAddDto = getRequest();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1)
                .description("Test description")
                .requestor(getRequester())
                .build();
        when(itemRequestService.getRequestById(eq(userId), eq(itemRequest.getId())))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest));
        mockMvc.perform(get("/requests/" + itemRequest.getId())
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()));
        verify(itemRequestService, times(1)).getRequestById(eq(userId), eq(itemRequest.getId()));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getRequestForUserTest() throws Exception {
        long userId = 1;
        ItemRequestAddDto itemRequestAddDto = getRequest();
        ItemRequest itemRequestOne = ItemRequest.builder()
                .id(1)
                .description("Test description")
                .requestor(getRequester())
                .build();
        ItemRequest itemRequestTwo = ItemRequest.builder()
                .id(2)
                .description("Test description")
                .requestor(getRequester())
                .build();
        List<ItemRequestDto> responseDtoList = Arrays.asList(
                ItemRequestMapper.toItemRequestDto(itemRequestOne),
                ItemRequestMapper.toItemRequestDto(itemRequestTwo)
        );
        when(itemRequestService.getRequestsForUser(eq(userId))).thenReturn(responseDtoList);
        mockMvc.perform(get("/requests")
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestOne.getId()))
                .andExpect(jsonPath("$[1].id").value(itemRequestTwo.getId()));
        verify(itemRequestService, times(1)).getRequestsForUser(eq(userId));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getAllRequestsWithoutParams() throws Exception {
        long userId = 1;
        ItemRequestAddDto itemRequestAddDto = getRequest();
        ItemRequest itemRequestOne = ItemRequest.builder()
                .id(1)
                .description("Test description")
                .requestor(getRequester())
                .build();
        ItemRequest itemRequestTwo = ItemRequest.builder()
                .id(2)
                .description("Test description")
                .requestor(getRequester())
                .build();
        List<ItemRequestDto> responseDtoList = Arrays.asList(
                ItemRequestMapper.toItemRequestDto(itemRequestOne),
                ItemRequestMapper.toItemRequestDto(itemRequestTwo)
        );
        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk());
    }

    private ItemRequestAddDto getRequest() {
        return ItemRequestAddDto.builder()
                .description("Test description")
                .build();
    }

    private User getRequester() {
        return User.builder()
                .id(1)
                .name("Testname")
                .email("email@test.ru")
                .build();
    }
}
