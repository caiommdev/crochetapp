package org.example.auth.application;

import org.example.auth.api.dtos.LoginRequest;
import org.example.auth.api.dtos.LoginResponse;
import org.example.auth.api.dtos.RegisterRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.domain.models.User;
import org.example.auth.domain.repository.UserRepository;
import org.example.auth.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_savesUserWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest("caio", "secret", "caio@example.com");
        when(userRepository.existsByUsername("caio")).thenReturn(false);
        when(userRepository.existsByEmail("caio@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.register(request);

        assertThat(response.username()).isEqualTo("caio");
        assertThat(response.email()).isEqualTo("caio@example.com");
        verify(userRepository).save(argThatPasswordHashEquals("hashed"));
    }

    @Test
    void register_throwsWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("caio", "secret", "caio@example.com");
        when(userRepository.existsByUsername("caio")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalStateException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("caio", "secret", "caio@example.com");
        when(userRepository.existsByUsername("caio")).thenReturn(false);
        when(userRepository.existsByEmail("caio@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalStateException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsTokenWhenCredentialsAreValid() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("caio")
                .email("caio@example.com")
                .passwordHash("hashed")
                .build();
        LoginRequest request = new LoginRequest("caio", "secret");
        when(userRepository.findByUsernameOrEmail("caio", "caio")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token123");

        LoginResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("token123");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void login_throwsWhenUserNotFound() {
        LoginRequest request = new LoginRequest("ghost", "secret");
        when(userRepository.findByUsernameOrEmail("ghost", "ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void login_throwsWhenPasswordDoesNotMatch() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("caio")
                .email("caio@example.com")
                .passwordHash("hashed")
                .build();
        LoginRequest request = new LoginRequest("caio", "wrong");
        when(userRepository.findByUsernameOrEmail("caio", "caio")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private User argThatPasswordHashEquals(String expected) {
        return org.mockito.ArgumentMatchers.argThat(user -> user.getPasswordHash().equals(expected));
    }
}
