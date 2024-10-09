package es.uclm.library.Business.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import es.uclm.library.Business.entity.Greeting;
import es.uclm.library.persistence.GreetingDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import es.uclm.library.persistence.GreetingDAO;

@Controller
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    @Autowired
    private GreetingDAO greetingDAO;

    @GetMapping("/greeting")
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        log.info(greetingDAO.findAll().toString());
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
        model.addAttribute("greeting", greeting);
        Greeting savedGreeting = greetingDAO.save(greeting);
        log.info("Saved greeting: " + savedGreeting);
        return "result";
    }
}
