package com.s.kai.login.LoginJwt.controllers;

import com.s.kai.login.LoginJwt.models.ERole;
import com.s.kai.login.LoginJwt.models.Role;
import com.s.kai.login.LoginJwt.models.User;
import com.s.kai.login.LoginJwt.payload.request.LoginRequest;
import com.s.kai.login.LoginJwt.payload.request.SignupRequest;
import com.s.kai.login.LoginJwt.payload.response.JwtResponse;
import com.s.kai.login.LoginJwt.payload.response.MessageResponse;
import com.s.kai.login.LoginJwt.repository.RoleRepository;
import com.s.kai.login.LoginJwt.repository.UserRepository;
import com.s.kai.login.LoginJwt.security.jwt.JwtUtils;
import com.s.kai.login.LoginJwt.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // like doFilterInternal
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("signin");

        // step2. use authenticationManager.authenticate(...) method to user authenticate
        //        while authentication throw exception
        //        while authentication success store user data, access in Authenticate DTO
        //
        //        AuthenticationManager.authenticate(...) method usually call UserDetailsService.loadUserByUsername(...) method
        //        to find user data
        Authentication authentication = authenticationManager.authenticate(
                // step1. transfer input username and password to UsernamePasswordAuthenticationToken DTO
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // step3. SecurityContextHolder store the authorized Authentication DTO
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 userDetails.getId(),
                                                 userDetails.getUsername(),
                                                 userDetails.getEmail(),
                                                 roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        System.out.println("signup");
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already use!"));
        }

        // Create new user's account
        User user = new User(signupRequest.getUsername(),
                             signupRequest.getEmail(),
                             encoder.encode(signupRequest.getPassword()));
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        break;
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
