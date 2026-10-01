package digiteen3play.setting;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                32,      // Salt length (bytes)
                64,      // Hash length (bytes)
                4,       // Parallelism (threads)
                65536,   // Memory cost (KB, 64MB here)
                3        // Iterations
        );
    }
}
