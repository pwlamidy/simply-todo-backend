package com.deepbluestudio.todobackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@OpenAPIDefinition(info=@Info(title="Todo API", description = "API for a todo app"))
@SpringBootApplication
public class TodoBackendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TodoBackendApplication.class, args);
    }

}
