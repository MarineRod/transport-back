package fr.diginamic.gestion_transport.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    private final String SECRET_KEY = "mySecretKeyForTestingPurposesOnly1234567890";
    private final long EXPIRE_IN = 3600;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "expireIn", EXPIRE_IN);
    }

    @Test
    void testGenerateToken_Success() {
        String username = "testuser";
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn((Collection) authorities);

        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject());
        assertNotNull(claims.get("roles"));
        assertInstanceOf(List.class, claims.get("roles"));

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testExtractUsername_Success() {
        // Given
        String username = "testuser";
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        String token = jwtUtil.generateToken(userDetails);

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String invalidToken = "invalid.token";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }


    @Test
    void testValidateToken_ValidToken() {
        String username = "testuser";
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        String token = jwtUtil.generateToken(userDetails);

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expireIn", -1L);

        String username = "testuser";
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        String expiredToken = jwtUtil.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateToken(expiredToken, userDetails);
        });
    }

}