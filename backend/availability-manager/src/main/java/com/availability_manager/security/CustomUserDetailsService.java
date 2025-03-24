package com.availability_manager.security;

import com.availability_manager.model.Login;
import com.availability_manager.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String anumber) throws UsernameNotFoundException {
        Login login = authRepository.findByEmployee_Anumber(anumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(login.getEmployeeAnumber())
                .password(login.getPassword())
                .authorities(new SimpleGrantedAuthority(String.valueOf(login.getRole())))// Añadimos el rol aquí
                .build();
    }
}
