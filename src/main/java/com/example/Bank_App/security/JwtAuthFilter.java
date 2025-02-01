package com.example.Bank_App.security;

import com.example.Bank_App.domain.dto.responses.ErrorResponse;
import com.example.Bank_App.services.TokenBlackListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenBlackListService tokenBlackListService;
    @Setter
    private List<String> publicEndpoints;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String email = null;
        final String requestURI = request.getRequestURI();

        // se l'endpoint è libero salta il filtro e lascia entrare
        if (isPublicUrl(requestURI)) {
            filterChain.doFilter(request,response); // ---> vai al filtro successivo
            return;
        }
        // verificare che il token inizi con "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendAuthErrorResponse(response, "MalformedTokenException", "Token JWT mancante o malformato");
            return;
        }
        jwt = authHeader.substring(7);
        //se ho fatto il logout
        if (tokenBlackListService.isPresentToken(jwt)) {
            sendAuthErrorResponse(response, "TokenExpiredException",
                    "Token nella blacklist, non è più valido!");
        }
        email = jwtService.extractUsername(jwt);
        // se il login è corretto inserisco il token nel contesto di sicurezza dell'applicazione
        // quindi in sostanza da quel momento il token sarà valido per l'autenticazione
        try {
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (MalformedJwtException e) {
            sendAuthErrorResponse(response, "MalformedTokenException", "Token JWT mancante o malformato");
        } catch (UsernameNotFoundException e) {
            sendAuthErrorResponse(response, "UsernameNotFoundException", "Username non trovato");
        }
        filterChain.doFilter(request,response);
    }

    private void sendAuthErrorResponse(HttpServletResponse response,
                                       String error,
                                       String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .exception(error)
                .message(message)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonReponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonReponse);
        response.getWriter().flush();
    }

    private boolean isPublicUrl(String requestURI) {
        return publicEndpoints.stream()
                .map(endpoint -> endpoint.replace("**", ".*"))
                .anyMatch(requestURI::matches);
    }

}
