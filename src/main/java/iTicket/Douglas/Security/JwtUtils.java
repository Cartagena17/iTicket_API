package iTicket.Douglas.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Calcula la huella HMAC-SHA256 del codigo usando el JWT_SECRET del servidor.
    //Se mezcla con el correo para que la huella solo sirva para ese usuario.
    public String hashCodigo(String correo, String codigo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(Decoders.BASE64.decode(jwtSecret), "HmacSHA256"));
            byte[] huella = mac.doFinal((correo + ":" + codigo).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(huella);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo procesar el codigo de verificacion", e);
        }
    }

    //Compara la huella del codigo ingresado con la guardada en el token.
    //MessageDigest.isEqual tarda lo mismo aunque falle al inicio o al final (evita ataques de tiempo).
    public boolean codigoCoincide(String correo, String codigoIngresado, String hashGuardado) {
        if (codigoIngresado == null || hashGuardado == null) return false;
        String hashIngresado = hashCodigo(correo, codigoIngresado.trim());
        return MessageDigest.isEqual(
                hashIngresado.getBytes(StandardCharsets.UTF_8),
                hashGuardado.getBytes(StandardCharsets.UTF_8));
    }

    public String create(Long idUsuario, String correo, String rol) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(correo)
                .claim("id", idUsuario)
                .claim("rol", rol)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseTokenAndClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        try {
            parseTokenAndClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public String createCodeVerificationToken(String correo, String codigo) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (1000 * 60 * 30));

        return Jwts.builder()
                .setSubject(correo)
                .claim("purpose", "CODE_VERIFICATION")
                .claim("codigoHash", hashCodigo(correo, codigo))
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRecoveryToken(String correo) {
        Date now = new Date();
        // 30 minutos = 1000 ms * 60 s * 30 m
        Date expiration = new Date(now.getTime() + (1000 * 60 * 30));

        return Jwts.builder()
                .setSubject(correo)
                .claim("purpose", "PASSWORD_RECOVERY")
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /** Segundos de vida del token, listo para ResponseCookie.maxAge(). */
    public long getExpirationSeconds() {
        return jwtExpirationMs / 1000;
    }
}

