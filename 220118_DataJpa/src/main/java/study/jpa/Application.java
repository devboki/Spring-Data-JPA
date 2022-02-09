package study.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public AuditorAware<String> auditorProvider(){
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor(){
				return Optional.of(UUID.randomUUID().toString());
			}
		};
	}
	
	//세션에서 userId 받아서 넣기
	
	/* ramda 
	 * 
	 * public AuditorAware<String> auditorProvider(){
	 * 	return () -> Optional.of(UUID.randomUUID().toString());
	 * }
	 * 
	 * */
}
