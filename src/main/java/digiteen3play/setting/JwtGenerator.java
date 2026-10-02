package digiteen3play.setting;


import digiteen3play.model.Person;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtGenerator {


    private final SecretKey signingKey;


    public JwtGenerator(@Value("${jwt.secret}") String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .subject(authentication.getName())
                .claim("email", user.getUsername())
                .claim("authorities",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

}
