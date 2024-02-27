package com.berkin.cryptoportfolio.controller;

import com.berkin.cryptoportfolio.dto.LoginDTO;
import com.berkin.cryptoportfolio.dto.SignupDTO;
import com.berkin.cryptoportfolio.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
            description = "Allows user to login to the system. It uses custom authentication system.",
            summary = "Login endpoint in order to be able to use the system",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "403"
                    )
            }
    )
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginDTO,request,response);
    }

    @PostMapping("/signup")
    @Operation(
            description = "It creates a new user and assigns the standard role to the user.",
            summary = "Creating a new account",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "403"
                    )
            }
    )
    public ResponseEntity signUp(@RequestBody SignupDTO signupDTO){
        return authService.signUp(signupDTO);
    }
}