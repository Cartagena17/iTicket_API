package iTicket.Douglas;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DouglasApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);


		System.out.println("DB_URL cargada: " + System.getProperty("DB_URL"));
		System.out.println("DB_USER cargada: " + System.getProperty("DB_USER"));

		SpringApplication.run(DouglasApplication.class, args);
	}
}
