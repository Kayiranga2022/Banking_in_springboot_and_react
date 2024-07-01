package com.banking;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Banking-App done by kayiranga",
                description = "Backend Rest APIs for Banking-App",
                version = "V1.0",
                contact = @Contact(
                        name = "kayiranga Ernest",
                        email = "kayinesta23@gmail.com",
                        url = "https://github.com/ernest2022/banking-app"

                ),
               license = @License(

                       name = "kayiranga Ernest",
                       url = "https://github.com/ernest2022/banking-app"

               )
        ),
        externalDocs = @ExternalDocumentation(
             description = "banking app Documentation",
                url = "https://github.com/ernest2022/banking-app"

        )
)

public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

}
