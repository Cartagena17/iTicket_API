package iTicket.Douglas.Utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encriptar(String claveTextoPlano){
        return encoder.encode(claveTextoPlano);
    }

    public boolean coincidence (String claveTextoPlano, String claveEncriptada){
        return encoder.matches(claveTextoPlano, claveEncriptada);
    }
}
