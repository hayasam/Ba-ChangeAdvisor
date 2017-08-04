package ch.uzh.ifi.seal.changeadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChangeadvisorApplication {

	public static void main(String[] args) {
//		new SpringApplicationBuilder(ChangeadvisorApplication.class).web(true).run(args);
        SpringApplication.run(ChangeadvisorApplication.class, args);
	}
}
