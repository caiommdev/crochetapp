package org.example.auth.application;

import org.example.auth.api.dtos.UpdateUserRequest;
import org.example.auth.api.dtos.UserResponse;
import org.example.auth.domain.models.User;
import org.example.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_mapsUsersToResponses() {
        User user = User.builder().id(UUID.randomUUID()).username("caio").email("caio@example.com").build();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("caio");
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(userService.findById(id)).isEmpty();
    }

    @Test
    void update_throwsWhenNewUsernameAlreadyTaken() {
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).username("caio").email("caio@example.com").build();
        UpdateUserRequest request = new UpdateUserRequest("novoUsername", "caio@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("novoUsername")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(id, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_throwsWhenNewEmailAlreadyTaken() {
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).username("caio").email("caio@example.com").build();
        UpdateUserRequest request = new UpdateUserRequest("caio", "novo@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("novo@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(id, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_updatesUsernameAndEmailWhenAvailable() {
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).username("caio").email("caio@example.com").build();
        UpdateUserRequest request = new UpdateUserRequest("novoUsername", "novo@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("novoUsername")).thenReturn(false);
        when(userRepository.existsByEmail("novo@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        Optional<UserResponse> result = userService.update(id, request);

        assertThat(result).isPresent();
        assertThat(result.get().username()).isEqualTo("novoUsername");
        assertThat(result.get().email()).isEqualTo("novo@example.com");
    }

    @Test
    void deleteById_throwsWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(id))
                .isInstanceOf(IllegalStateException.class);

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_deletesWhenUserExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteById(id);

        verify(userRepository).deleteById(id);
    }
}
