package com.java.tp_principal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TpPrincipalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TpPrincipalBackendApplication.class, args);
	}

}
