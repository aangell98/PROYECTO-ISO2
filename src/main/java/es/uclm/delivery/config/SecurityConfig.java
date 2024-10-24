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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/home", "/css/**", "/js/**", "/images/**").permitAll() // Permitir acceso
                                                                                                 // público a /home
                        .requestMatchers("/homeCliente/**").hasRole("CLIENTE")
                        .requestMatchers("/homeRepartidor/**").hasRole("REPARTIDOR")
                        .requestMatchers("/homeRestaurante/**").hasRole("RESTAURANTE")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            String role = authentication.getAuthorities().iterator().next().getAuthority();

                            // Log del usuario autenticado
                            Logger log = LoggerFactory.getLogger(SecurityConfig.class);
                            log.info("Usuario autenticado: " + username);
                            log.info("Rol asignado: " + role);

                            // Manejar la sesión
                            HttpSession session = request.getSession();
                            session.setAttribute("username", username);
                            session.setAttribute("role", role);
                            session.setMaxInactiveInterval(30 * 60); // La sesión expira en 30 minutos de inactividad

                            log.info("Sesión creada: " + session.getId());
                            log.info("Atributos de la sesión: username=" + session.getAttribute("username") + ", role="
                                    + session.getAttribute("role"));

                            // Redireccionar según el rol
                            if (role.equals("ROLE_CLIENTE")) {
                                log.info("Redirigiendo a /homeCliente");
                                response.sendRedirect("/homeCliente");
                            } else if (role.equals("ROLE_REPARTIDOR")) {
                                log.info("Redirigiendo a /homeRepartidor");
                                response.sendRedirect("/homeRepartidor");
                            } else if (role.equals("ROLE_RESTAURANTE")) {
                                log.info("Redirigiendo a /homeRestaurante");
                                response.sendRedirect("/homeRestaurante");
                            } else {
                                log.info("Redirigiendo a /home por rol desconocido");
                                response.sendRedirect("/home");
                            }
                        })

                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler()) // Usamos un handler personalizado
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true) // Invalida la sesión de usuario
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
            private final Logger log = LoggerFactory.getLogger(LogoutSuccessHandler.class);

            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException {
                if (authentication != null) {
                    String username = authentication.getName();
                    log.info("Usuario '{}' ha cerrado sesión exitosamente.", username);
                } else {
                    log.info("Cierre de sesión sin usuario autenticado.");
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
