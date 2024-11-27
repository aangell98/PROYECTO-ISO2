package es.uclm.delivery.config;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.slf4j.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/home", "/buscar_restaurantes", "/restaurantes_destacados", "/registroCliente", "/registroRepartidor", "/registroRestaurante", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/homeCliente/**", "/realizarPedido/**", "/realizar_pedido", "/confirmar_pedido").hasRole("CLIENTE")
                .requestMatchers("/homeRepartidor/**", "/repartidor", "/autoasignar/{pedidoId}").hasRole("REPARTIDOR")
                .requestMatchers("/homeRestaurante/**", "/eliminarNombreDireccionRestaurante/**").hasRole("RESTAURANTE")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // Verifica si hay una URL solicitada antes de la autenticación
                    SavedRequest savedRequest = requestCache.getRequest(request, response);

                    if (savedRequest != null) {
                        // Redirige a la URL almacenada en la cache
                        response.sendRedirect(savedRequest.getRedirectUrl());
                        return;
                    }

                    // Redirección según el rol si no hay URL almacenada
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    if (role.equals("ROLE_CLIENTE")) {
                        response.sendRedirect("/homeCliente");
                    } else if (role.equals("ROLE_REPARTIDOR")) {
                        response.sendRedirect("/homeRepartidor");
                    } else if (role.equals("ROLE_RESTAURANTE")) {
                        response.sendRedirect("/homeRestaurante");
                    } else {
                        response.sendRedirect("/home");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessHandler(customLogoutSuccessHandler())
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .permitAll())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
        handler.setErrorPage("/403");
        return handler;
    }

    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            private final Logger log = LoggerFactory.getLogger(this.getClass());

            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException {
                if (authentication != null) {
                    String username = authentication.getName();
                    log.info("Usuario '{}' ha cerrado sesion exitosamente.", username);
                } else {
                    log.info("Cierre de sesion sin usuario autenticado.");
                }

                // Redirigir al usuario a la página de login con el parámetro de logout=true
                response.sendRedirect("/home?logout=true");
            }
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider);
    }
}
