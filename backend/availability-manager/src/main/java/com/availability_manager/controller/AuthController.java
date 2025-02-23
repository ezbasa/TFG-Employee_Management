package com.availability_manager.controller;

import com.availability_manager.model.Login;
import com.availability_manager.service.AuthService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private  final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Login login) {
        if (authService.registerLogin(login) != null) {
            return ResponseEntity.ok("User registered successfully");
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        String token = authService.authenticateAndGenerateToken(login);
        Login loginRole = authService.getEmployee(login.getEmployeeAnumber());
        String role = loginRole.getRole().toString();

        return ResponseEntity.ok(Map.<String, String>of("token", token, "role", role));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            authService.logout(token);
            return ResponseEntity.ok("User logged out successfully");
        }
        return ResponseEntity.badRequest().build();
    }

    //manejadores de excepciones
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleException(EntityExistsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
