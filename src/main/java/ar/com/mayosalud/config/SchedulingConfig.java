package ar.com.mayosalud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita las tareas programadas (recordatorios automáticos por email). */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
