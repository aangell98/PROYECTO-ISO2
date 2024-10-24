package es.uclm.delivery.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ManejoErrores implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletResponse response) {
        // Redirigir a login si el error es 403 (Forbidden)
        if (response.getStatus() == 403) {
            return "redirect:/login";
        }
        // Puedes manejar otros errores como 404 o 500 aquí si lo deseas
        return "error"; // Página de error genérica
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "redirect:/login";  // O podrías mostrar una página de acceso denegado personalizada si lo prefieres
    }
}
