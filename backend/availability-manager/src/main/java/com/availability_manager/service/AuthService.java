package com.availability_manager.service;

import com.availability_manager.model.Login;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface AuthService {

    List<Login> getAllLogins();

    Login registerLogin(Login login);

    Login updateLogin(Login login);

    void deleteLogin(@NotBlank String anumber);

    String authenticateAndGenerateToken(Login login);

    Login getEmployee(String anumber);

    void logout(String token);

    boolean isTokenBlacklisted(String token);
}
