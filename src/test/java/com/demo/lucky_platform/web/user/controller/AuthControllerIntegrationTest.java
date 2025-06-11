package com.demo.lucky_platform.web.user.controller;

import com.demo.lucky_platform.web.controller.BaseControllerIntegrationTest;
import com.demo.lucky_platform.web.user.dto.LoginForm;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import com.demo.lucky_platform.web.user.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuthController.
 */
@DisplayName("Auth Controller Integration Tests")
class AuthControllerIntegrationTest extends BaseControllerIntegrationTest {

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Login - Success")
    void login_Success() throws Exception {
        // Given
        LoginForm loginForm = new LoginForm("test@example.com", "password");
        doNothing().when(authService).login(any(LoginForm.class), any(MockHttpServletResponse.class));

        // When & Then
        mockMvc.perform(post("/api/auth/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Reissue Access Token - Success")
    void reissueAccessToken_Success() throws Exception {
        // Given
        doNothing().when(authService).reissueAccessToken(any(), any());

        // When & Then
        mockMvc.perform(post("/api/auth/v1/reissue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Logout - Success")
    void logout_Success() throws Exception {
        // Given
        doNothing().when(authService).logout(any(), any());

        // When & Then
        mockMvc.perform(post("/api/auth/v1/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @DisplayName("Create Authorization Header - Success")
    void createAuthorizationHeader_Success() throws Exception {
        // Given
        String accessToken = "Bearer test-access-token";

        // Mock the AuthService to set the Authorization header
        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
            return null;
        }).when(authService).setAuthorizationHeader(any(), any());

        // When & Then
        mockMvc.perform(get("/api/auth/v1/socket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
