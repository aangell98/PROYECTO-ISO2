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

        if (response.getStatus() == 403) {
            return "redirect:/login";
        }

        return "error";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "redirect:/login";
    }
}
