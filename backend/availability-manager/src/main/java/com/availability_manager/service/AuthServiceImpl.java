package com.availability_manager.service;

import com.availability_manager.model.Employee;
import com.availability_manager.model.Login;
import com.availability_manager.repository.AuthRepository;
import com.availability_manager.security.JwtProvider;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService/*implements UserDetailsService */{

    private final AuthRepository authRepository;
    private final JwtProvider jwtProvider;
    private final AuthServiceManagement authServiceManagement;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet(); // Concurrency-safe set

    //PARTE LOGIN

    @Override
    public List<Login> getAllLogins(){
        return authRepository.getAllLogins();
    }

    //Registrar un usuario por primera vez (administrador)
    @Override
    public Login registerLogin(Login login) {
        String anumber = login.getEmployeeAnumber();

        if(existsUserLogin(anumber)) {
            throw new EntityExistsException("User already exist");
        }
        Employee emp = authServiceManagement.getEmployee(anumber).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Login newLogin = new Login();
        newLogin.setEmployee(emp);
        String hashedPassword = passwordEncoder.encode(login.getPassword());
        newLogin.setPassword(hashedPassword);
        newLogin.setRole(login.getRole());

        return authRepository.save(newLogin);
    }

    @Override
    public Login updateLogin(Login l) {
        Login login = authRepository.findByEmployee_Anumber(l.getEmployeeAnumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        login.setRole(l.getRole());
        return authRepository.save(login);
    }

    /**
     * Comprueba si un usuario existe en la tabla Login
     * @param anumber
     * @return boolean
     */
    private boolean existsUserLogin(String anumber) {
        return authRepository.findByEmployee_Anumber(anumber).isPresent();
    }

    @Override
    public String authenticateAndGenerateToken(Login receivedLogin) {
        Login login = authRepository.findByEmployee_Anumber(receivedLogin.getEmployeeAnumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(receivedLogin.getPassword(), login.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Generar y devolver el token JWT
        return jwtProvider.generateToken(userDetailsService.loadUserByUsername(login.getEmployeeAnumber()));
    }

    @Override
    public Login getEmployee(String anumber) {
        return authRepository.findByEmployee_Anumber(anumber).get();
    }

    //LOGOUT
    @Override
    public void logout(String token) {
        blacklistedTokens.add(token);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
