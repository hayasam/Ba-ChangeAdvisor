package ch.uzh.ifi.seal.changeadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ChangeadvisorApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ChangeadvisorApplication.class).web(true).run(args);
		SpringApplication.run(ChangeadvisorApplication.class, args);
	}
}
