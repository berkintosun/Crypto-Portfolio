package com.berkin.cryptoportfolio.controller;

import com.berkin.cryptoportfolio.dto.LoginDTO;
import com.berkin.cryptoportfolio.dto.SignupDTO;
import com.berkin.cryptoportfolio.entity.auth.User;
import com.berkin.cryptoportfolio.repository.RoleRepository;
import com.berkin.cryptoportfolio.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityContextRepository securityContextRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            SecurityContextHolder.getContext().setAuthentication(null);
            logger.error("Login Failure", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody SignupDTO signupDTO){
        if(!userRepository.findByUsername(signupDTO.getUsername()).isEmpty()){
            return new ResponseEntity<>("Username exist", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByName("ROLE_ADMIN"))); //@TODO role improvement
        userRepository.save(user);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}