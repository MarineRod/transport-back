package fr.diginamic.gestion_transport.security;

import fr.diginamic.gestion_transport.entites.Role;
import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Given
        String username = "testuser";
        String password = "password123";
        boolean enabled = true;

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setEnabled(enabled);
        mockUser.setRoles(roles);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(enabled, result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());

        // Vérifier les autorités
        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

        // Vérifier l'appel au repository
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        // Given
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserWithNoRoles_ReturnsUserDetailsWithEmptyAuthorities() {
        // Given
        String username = "userroles";
        String password = "password123";
        boolean enabled = true;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setEnabled(enabled);
        mockUser.setRoles(new HashSet<>()); // Pas de rôles

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(enabled, result.isEnabled());
        assertTrue(result.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_DisabledUser_ReturnsDisabledUserDetails() {
        // Given
        String username = "disableduser";
        String password = "password123";
        boolean enabled = false;

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setEnabled(enabled);
        mockUser.setRoles(roles);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertFalse(result.isEnabled()); // Utilisateur désactivé
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());

        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_NullUsername_CallsRepository() {
        // Given
        String username = null;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_EmptyUsername_CallsRepository() {
        // Given
        String username = "";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_RepositoryThrowsException_ExceptionPropagated() {
        // Given
        String username = "testuser";
        RuntimeException expectedException = new RuntimeException("Database error");
        when(userRepository.findByUsername(username)).thenThrow(expectedException);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }
}