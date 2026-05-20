package ar.com.mayosalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManualController {

    @GetMapping("/manual")
    public String manual() {
        return "manual/manual-usuario";
    }
}
