package org.nure.atark.autoinsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.nure.atark.autoinsure.dto.LoginRequest;
import org.nure.atark.autoinsure.entity.User;
import org.nure.atark.autoinsure.service.JwtService;
import org.nure.atark.autoinsure.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Register a new user", description = "Create a new user and return a JWT token upon successful registration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully with token",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"token\": \"<jwt_token>\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid user data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            String token = jwtService.generateToken(registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("{ \"token\":\"" + token + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Login a user", description = "Authenticate a user and return a JWT token upon successful login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully with token",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"token\":\"<jwt_token>\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"string\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            User loggedInUser = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtService.generateToken(loggedInUser);
            return ResponseEntity.ok("{\"token\":\"" + token + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
