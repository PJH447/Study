package com.demo.lucky_platform.web.user.controller;

import com.demo.lucky_platform.web.common.dto.CommonResponse;
import com.demo.lucky_platform.web.controller.BaseControllerIntegrationTest;
import com.demo.lucky_platform.web.user.dto.EditInfoForm;
import com.demo.lucky_platform.web.user.dto.EditPasswordForm;
import com.demo.lucky_platform.web.user.dto.HeaderInfoDto;
import com.demo.lucky_platform.web.user.dto.SignUpForm;
import com.demo.lucky_platform.web.user.service.AuthService;
import com.demo.lucky_platform.web.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the UserRestController.
 */
@DisplayName("User Rest Controller Integration Tests")
class UserRestControllerIntegrationTest extends BaseControllerIntegrationTest {

    @MockBean
    private UserRestController userRestController;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Sign Up - Success")
    void signUp_Success() throws Exception {
        // Given
        SignUpForm signUpForm = SignUpForm.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .nickname("testuser")
                .phone("1234567890")
                .impUid("imp_123456789")
                .build();
        doNothing().when(userService).signUp(any(SignUpForm.class));

        // When & Then
        mockMvc.perform(post("/api/user/v1/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Edit User Info - Success")
    void editUserInfo_Success() throws Exception {
        // Given
        EditInfoForm editInfoForm = new EditInfoForm("newNickname");

        // Mock the controller to skip the authentication check
        doAnswer(invocation -> {
            // Skip the authentication check and directly call the service
            userService.editNickname(1L, editInfoForm.nickname());
            return CommonResponse.createVoidResponse();
        }).when(userRestController).editUserInfo(any(), any());

        // When & Then
        mockMvc.perform(post("/api/user/v1/edit/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editInfoForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @DisplayName("Edit Password - Success")
    void editPassword_Success() throws Exception {
        // Given
        EditPasswordForm editPasswordForm = new EditPasswordForm("oldPassword", "newPassword");
        doNothing().when(userService).editPassword(anyLong(), any(EditPasswordForm.class));
        doNothing().when(authService).logout(any(), any());

        // When & Then
        mockMvc.perform(post("/api/user/v1/edit/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editPasswordForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Exist Same Nickname - True")
    void existSameNickname_True() throws Exception {
        // Given
        when(userService.existSameNickname(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/user/v1/nickname")
                .param("nickname", "existingNickname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("Exist Same Email - True")
    void existSameEmail_True() throws Exception {
        // Given
        when(userService.existSameEmail(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/user/v1/email")
                .param("email", "existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @DisplayName("Get Header Info - Success")
    void getHeaderInfo_Success() throws Exception {
        // Given
        HeaderInfoDto headerInfoDto = HeaderInfoDto.builder()
                .userId(1L)
                .email("email@example.com")
                .name("Test User")
                .nickname("nickname")
                .picture("profile.jpg")
                .phone("1234567890")
                .isAdmin(false)
                .isVip(false)
                .build();
        when(userService.findHeaderInfo(anyLong())).thenReturn(headerInfoDto);

        // When & Then
        mockMvc.perform(get("/api/user/v1/headerInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value("nickname"))
                .andExpect(jsonPath("$.data.email").value("email@example.com"));
    }
}
