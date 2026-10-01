package iTicket.Douglas;

import java.util.TimeZone;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DouglasApplication {

	public static void main(String[] args) {

		//Cargar el env solo localmente. si no existe (Heroku), lo ignora
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();


		dotenv.entries().forEach(entry -> {
			// Solo asigna a System Property si no ah sido definida por el entorno real del servidor
			if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
			System.setProperty(entry.getKey(), entry.getValue());}
		});


		System.out.println("DB_URL cargada: " + System.getProperty("DB_URL"));
		System.out.println("DB_USER cargada: " + System.getProperty("DB_USER"));

		// Para que tome en cuenta la hora de aqui, antes estaba en UTC y nosotros somos UTC-6 (6 horas atrasados)
		TimeZone.setDefault(TimeZone.getTimeZone("America/El_Salvador"));

		//Arrancar la API
		SpringApplication.run(DouglasApplication.class, args);
	}
}
