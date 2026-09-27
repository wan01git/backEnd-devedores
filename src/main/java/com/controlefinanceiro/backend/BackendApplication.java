package com.controlefinanceiro.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication e um "atalho" que junta 3 anotacoes:
// - @Configuration: esta classe pode declarar beans (objetos gerenciados pelo Spring)
// - @EnableAutoConfiguration: o Spring configura sozinho o que estiver no pom.xml
//   (ao ver o driver do Postgres + starter-data-jpa, ele ja prepara o JPA pra voce)
// - @ComponentScan: o Spring varre este pacote e os subpacotes procurando classes
//   marcadas com @Controller, @Service, @Repository etc. para instanciar e conectar
//   automaticamente (isso e Inversao de Controle / Injecao de Dependencia)
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
