package fr.diginamic.gestion_transport.controllers.authentification;

import fr.diginamic.gestion_transport.entites.User;
import fr.diginamic.gestion_transport.repositories.UserRepository;
import fr.diginamic.gestion_transport.security.CustomUserDetailsService;
import fr.diginamic.gestion_transport.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur en charge de l'authentification
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * authenticationManager : permet d'authentifier l'utilisateur
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Pour générer un token lors de l'authentification
     */
    private final JwtUtil jwtUtil;

    /**
     * Permet d'aller cherche en base les infos utilisateur
     */
    private final CustomUserDetailsService userDetailsService;

    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint de LOGIN qui reçoit un body contenant 2 infos : username et password (non crypté)
     *
     * @param authRequest le body de la requête HTTP
     * @return {@link ResponseEntity}
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);

            User user = this.userRepository.findByUsername(authRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found: " + authRequest.getUsername()));

            return ResponseEntity.ok(new AuthResponse(jwt, user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        }
    }
}