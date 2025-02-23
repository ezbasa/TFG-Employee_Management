package com.availability_manager.security.filters;

import com.availability_manager.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OwnershipValidationFilter extends OncePerRequestFilter {  //HACER PRUEBAS CON ESTE FILTRO

    private final JwtProvider jwtProvider;
    //private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        // Verificar si la ruta comienza con "/item-calendar"
        if (!request.getRequestURI().startsWith("/item-calendar") || request.getRequestURI().startsWith("/item-calendar") && method.equals("GET")) {
            filterChain.doFilter(request, response); // Continuar sin aplica el filtro
            return;
        }

        // Lee el token desde los headers
        String token = extractToken(request); // Implementa esta función según tu lógica
        if(token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token no proporcionado o invalido");
            return;
        }
        String tokenAnumber = jwtProvider.extractAnumber(token);

        if("DELETE".equalsIgnoreCase(request.getMethod())) {
            if(!checkParams(request, response, tokenAnumber))
                return;
        }else{
            if(!checkBody(request, response,tokenAnumber))
                return;
        }

        // Continúa con el siguiente filtro o controlador
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replaceFirst("Bearer ", "");
            return token;
        }

        return null;
    }

    private boolean checkParams(HttpServletRequest request, HttpServletResponse response, String tokenAnumber) throws IOException {
        String Anumber = request.getParameter("anumber");

        if (!Anumber.equals(tokenAnumber)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("No tienes permiso para modificar este recurso");
            return false;
        }
        return true;
    }

    private boolean checkBody (HttpServletRequest request, HttpServletResponse response, String tokenAnumber) throws IOException {

        String requestBody = new String(request.getInputStream().readAllBytes());
        if (requestBody.isEmpty() || requestBody.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("El cuerpo de la solicitud no puede estar vacío");
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> bodyMap = objectMapper.readValue(requestBody, Map.class);

        // Valida que el anumber del body coincida con el del token
        String requestAnumber = (String) bodyMap.get("employeeAnumber");
        if (requestAnumber == null || !requestAnumber.equals(tokenAnumber)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("No tienes permiso para modificar este recurso");
            return false;
        }
        return true;
    }
}
