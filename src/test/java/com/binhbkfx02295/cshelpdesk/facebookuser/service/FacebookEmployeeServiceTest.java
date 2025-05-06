package com.binhbkfx02295.cshelpdesk.facebookuser.service;

import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.repository.FacebookUserRepository;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FacebookEmployeeServiceTest {

    @Mock
    private FacebookUserRepository facebookUserRepository;

    @InjectMocks
    private FacebookUserServiceImpl facebookUserService;

    private final String TEST_ID = "fb_001";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_Success() throws Exception {
        FacebookUser user = FacebookUser.builder()
                .facebookId(TEST_ID)
                .fullName("John Doe")
                .facebookFirstName("John")
                .facebookLastName("Doe")
                .email("john@example.com")
                .build();

        when(facebookUserRepository.save(user)).thenReturn(user);

        APIResultSet<?> result = facebookUserService.save(user);

        assertEquals(HttpStatus.OK.value(), result.getHttpCode());
        assertEquals(FacebookUserService.MSG_SUCCESS, result.getMessage());
    }

    @Test
    void testUpdate_Success() throws Exception {
        FacebookUser existing = FacebookUser.builder()
                .facebookId(TEST_ID)
                .fullName("John Doe")
                .build();

        when(facebookUserRepository.get(TEST_ID)).thenReturn(existing);
        when(facebookUserRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        existing.setFullName("Johnny Doe");

        APIResultSet<FacebookUserDTO> result = facebookUserService.update(existing);

        assertEquals(HttpStatus.OK.value(), result.getHttpCode());
        assertEquals("Johnny Doe", result.getData().getFullName());
    }

    @Test
    void testUpdate_UserNotFound() throws Exception {
        when(facebookUserRepository.get("not_exist")).thenReturn(null);

        FacebookUser ghost = FacebookUser.builder()
                .facebookId("not_exist")
                .fullName("Ghost")
                .build();

        APIResultSet<?> result = facebookUserService.update(ghost);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getHttpCode());
        assertEquals(FacebookUserService.MSG_ERROR_USER_NOT_FOUND, result.getMessage());
    }

    @Test
    void testSearch_UserFound() throws Exception {
        FacebookUser user = FacebookUser.builder()
                .facebookId(TEST_ID)
                .fullName("Johnny Doe")
                .build();

        when(facebookUserRepository.search("johnny")).thenReturn(List.of(user));

        APIResultSet<List<FacebookUserDTO>> result = facebookUserService.search("johnny");

        assertEquals(HttpStatus.OK.value(), result.getHttpCode());
        assertEquals(1, result.getData().size());
        assertEquals("Johnny Doe", result.getData().get(0).getFullName());
    }

    @Test
    void testSearch_UserNotFound() throws Exception {
        when(facebookUserRepository.search("unknown")).thenReturn(List.of());

        APIResultSet<List<FacebookUserDTO>> result = facebookUserService.search("unknown");

        assertEquals(HttpStatus.OK.value(), result.getHttpCode());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void testGet_UserExists() throws Exception {
        FacebookUser user = FacebookUser.builder()
                .facebookId(TEST_ID)
                .fullName("Johnny Doe")
                .build();

        when(facebookUserRepository.get(TEST_ID)).thenReturn(user);

        APIResultSet<FacebookUserDTO> result = facebookUserService.get(TEST_ID);

        assertEquals(HttpStatus.OK.value(), result.getHttpCode());
        assertEquals("Johnny Doe", result.getData().getFullName());
    }

    @Test
    void testGet_UserNotFound() throws Exception {
        when(facebookUserRepository.get("not_exist")).thenReturn(null);

        APIResultSet<FacebookUserDTO> result = facebookUserService.get("not_exist");

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getHttpCode());
        assertEquals(FacebookUserService.MSG_ERROR_USER_NOT_FOUND, result.getMessage());
    }
}
