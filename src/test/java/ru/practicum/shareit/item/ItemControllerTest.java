package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentAddDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.BaseConstants;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItemSuccessTest() throws Exception {
        long userId = 1;
        ItemDto requestDto = getRequestDto();
        ItemDto responseDto = getItemResponseDto(1);
        when(itemService.addItem(any(ItemDto.class), eq(1))).thenReturn(responseDto);
        mockMvc.perform(post("/items")
                        .header(BaseConstants.HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).addItem(any(ItemDto.class), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemByIdSuccessTest() throws Exception {
        long userId = 1;
        ItemDto responseDto = getItemResponseDto(1);
        when(itemService.getItemById(eq(userId), eq(responseDto.getId()))).thenReturn(responseDto);
        mockMvc.perform(get("/items/" + responseDto.getId())
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk());
        verify(itemService, times(1)).getItemById(eq(userId), eq(responseDto.getId()));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllItemsForOwnerSuccessTest() throws Exception {
        long userId = 1;
        ItemDto responseDtoOne = getItemResponseDto(1);
        ItemDto responseDtoTwo = getItemResponseDto(2);
        List<ItemDto> responseDtoList = Arrays.asList(
                responseDtoOne,
                responseDtoTwo
        );
        when(itemService.getItems(eq(userId))).thenReturn(responseDtoList);
        mockMvc.perform(get("/items")
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk());
        verify(itemService, times(1)).getItems(eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllItemsBySearchTextSuccessTest() throws Exception {
        long userId = 1;
        ItemDto responseDtoOne = getItemResponseDto(1);
        ItemDto responseDtoTwo = getItemResponseDto(2);
        List<ItemDto> responseDtoList = Arrays.asList(
                responseDtoOne,
                responseDtoTwo
        );
        when(itemService.searchItems(anyLong(), anyString())).thenReturn(responseDtoList);
        mockMvc.perform(get("/items/search")
                        .header(BaseConstants.HEADER, userId)
                        .param("text", "someText"))
                .andExpect(status().isOk());
        verify(itemService, times(1)).searchItems(anyLong(), eq("someText"));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItemSuccessTest() throws Exception {
        long userId = 1;
        long itemId = 1;
        ItemDto requestDto = getRequestDto();
        ItemDto responseDto = getItemResponseDto(1);
        when(itemService.updateItem(eq(itemId), eq(userId), any(ItemDto.class))).thenReturn(responseDto);
        mockMvc.perform(patch("/items/" + itemId)
                        .header(BaseConstants.HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).updateItem(eq(itemId), eq(userId), any(ItemDto.class));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void createCommentTest() throws Exception {
        long userId = 1;
        long itemId = 1;
        CommentDto requestDto = CommentDto.builder()
                .text("Коммент на щетку")
                .build();
        CommentDto responseDto = CommentDto.builder()
                .id(1L)
                .build();
        when(itemService.addComment(any(CommentAddDto.class), eq(userId), eq(itemId))).thenReturn(responseDto);
        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(BaseConstants.HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).addComment(any(CommentAddDto.class), eq(userId), eq(itemId));
        verifyNoMoreInteractions(itemService);
    }



    private ItemDto getItemResponseDto(long id) {
        return ItemDto.builder()
                .id(id)
                .build();
    }

    private ItemDto getRequestDto() {
        return ItemDto.builder()
                .name("Фотоаппарат")
                .description("Без флешки")
                .available(true)
                .build();
    }
}
