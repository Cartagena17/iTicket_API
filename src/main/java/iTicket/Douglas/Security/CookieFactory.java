package iTicket.Douglas.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieFactory {

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.samesite}")
    private String cookieSameSite;

    public ResponseCookie build(String token, long maxAgeSeconds) {
        return ResponseCookie.from("authToken", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    /** Cookie vacia con maxAge=0 para logout — el navegador la elimina. */
    public ResponseCookie clear() {
        return ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0)
                .build();
    }
}
