package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setId(1L);
    }

    @Test
    void getConnectedUser_UserExists() {
        String username = "testuser";
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When
            User result = userService.getConnectedUser();

            // Then
            assertNotNull(result);
            assertEquals(testUser.getUsername(), result.getUsername());
            assertEquals(testUser.getId(), result.getId());
            verify(userRepository).findByUsername(username);
        }
    }

    @Test
    void getConnectedUser_UserNotExists() {
        String username = "testuser2";
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userService.getConnectedUser()
            );

            assertEquals("Utilisateur non trouvé: " + username, exception.getMessage());
            verify(userRepository).findByUsername(username);
        }
    }
}