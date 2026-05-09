package ar.com.mayosalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Sirve la página de política de privacidad (Ley 25.326 / Ley 26.529), accesible sin autenticación. */
@Controller
@RequestMapping("/politica-privacidad")
public class PoliticaPrivacidadController {

    @GetMapping
    public String ver() {
        return "politica-privacidad";
    }
}
