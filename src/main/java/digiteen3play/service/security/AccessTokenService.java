package digiteen3play.service.security;


import digiteen3play.pvm.AuthenticationResponsePVM;
import digiteen3play.pvm.LoginPVM;
import digiteen3play.setting.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccessTokenService {


    private final JwtGenerator jwtGenerator;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponsePVM authenticateUser(LoginPVM loginPVM) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginPVM.getEmail(),
                                loginPVM.getPassword()));
        String token = jwtGenerator.generateToken(authentication);
        return new AuthenticationResponsePVM(token);
    }

}
