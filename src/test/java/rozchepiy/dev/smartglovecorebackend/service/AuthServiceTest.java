package rozchepiy.dev.smartglovecorebackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import rozchepiy.dev.smartglovecorebackend.dto.auth.AuthResponse;
import rozchepiy.dev.smartglovecorebackend.dto.auth.RegisterRequest;
import rozchepiy.dev.smartglovecorebackend.model.User;
import rozchepiy.dev.smartglovecorebackend.repository.UserRepository;
import rozchepiy.dev.smartglovecorebackend.security.JwtService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldReturnToken_WhenUserIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@lpnu.ua");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@lpnu.ua")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_pass");
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@lpnu.ua");

        when(userRepository.existsByEmail("test@lpnu.ua")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Користувач з таким email вже існує", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}