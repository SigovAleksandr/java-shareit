package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.BaseConstants;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void getBookingByIdSuccessTest() throws Exception {
        long userId = 1;
        BookingDto responseDto = getBooking(1);
        when(bookingService.getBookingById(eq(responseDto.getId()), eq(userId))).thenReturn(responseDto);
        mockMvc.perform(get("/bookings/" + responseDto.getId())
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk());
        verify(bookingService, times(1)).getBookingById(eq(responseDto.getId()), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createBookingSuccessTest() throws Exception {
        long userId = 1;
        BookingAddDto requestDto = BookingAddDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(1))
                .itemId(1)
                .build();
        BookingDto responseDto = getBooking(1);
        when(bookingService.addBooking(any(BookingAddDto.class), eq(userId))).thenReturn(responseDto);
        ResultActions resultActions = mockMvc.perform(post("/bookings")
                        .header(BaseConstants.HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookingService, times(1)).addBooking(any(BookingAddDto.class), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingSuccessTest() throws Exception {
        long userId = 1;
        BookingDto responseDto = getBooking(1);
        when(bookingService.approveBooking(eq(responseDto.getId()), eq(userId), anyBoolean())).thenReturn(responseDto);
        mockMvc.perform(patch("/bookings/" + responseDto.getId())
                        .header(BaseConstants.HEADER, userId)
                        .param("approved", "false"))
                .andExpect(status().isOk());
        verify(bookingService, times(1)).approveBooking(eq(responseDto.getId()), eq(userId),
                eq(false));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllByStateTest() throws Exception {
        long userId = 1;
        BookingDto responseDtoOne = getBooking(1);
        BookingDto responseDtoTwo = getBooking(2);
        List<BookingDto> responseDtoList = Arrays.asList(
                responseDtoOne,
                responseDtoTwo
        );
        when(bookingService.getUserBooking(any(), eq(userId), eq(0), eq(2))).thenReturn(responseDtoList);
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=2")
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtoOne.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDtoTwo.getId()));
        verify(bookingService, times(1)).getUserBooking(eq("ALL"), eq(userId), eq(0), eq(2));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllByStateForOwnerWithFromAndSizeTest() throws Exception {
        long userId = 1;
        BookingDto responseDtoOne = getBooking(1);
        BookingDto responseDtoTwo = getBooking(2);
        List<BookingDto> responseDtoList = Arrays.asList(
                responseDtoOne,
                responseDtoTwo
        );
        when(bookingService.getUserItemBooking(any(), eq(userId), eq(0), eq(2))).thenReturn(responseDtoList);
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=2")
                        .header(BaseConstants.HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtoOne.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDtoTwo.getId()));
        verify(bookingService, times(1)).getUserItemBooking(eq("ALL"), eq(userId), eq(0), eq(2));
        verifyNoMoreInteractions(bookingService);
    }

    private BookingDto getBooking(long id) {
        return BookingDto.builder()
                .id(id)
                .build();
    }
}
